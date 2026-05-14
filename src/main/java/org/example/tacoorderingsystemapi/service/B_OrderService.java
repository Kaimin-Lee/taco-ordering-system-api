package org.example.tacoorderingsystemapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.tacoorderingsystemapi.dto.ManualOrderDTO;
import org.example.tacoorderingsystemapi.entity.OrderInfo;
import org.example.tacoorderingsystemapi.entity.OrderItem;
import org.example.tacoorderingsystemapi.entity.Product;
import org.example.tacoorderingsystemapi.mapper.OrderInfoMapper;
import org.example.tacoorderingsystemapi.mapper.OrderItemMapper;
import org.example.tacoorderingsystemapi.mapper.ProductMapper;
import org.example.tacoorderingsystemapi.util.OrderNoGenerator;
import org.example.tacoorderingsystemapi.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class B_OrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderNoGenerator orderNoGenerator;

    public List<OrderInfo> getTodayOrders() {
        return getOrdersByCondition("DATE(create_time) = CURDATE()");
    }

    public List<OrderInfo> getOrdersByPeriod(String period) {
        String condition = switch (period) {
            case "today" -> "DATE(create_time) = CURDATE()";
            case "week" -> "YEARWEEK(create_time, 1) = YEARWEEK(NOW(), 1)";
            case "month" -> "DATE_FORMAT(create_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')";
            case "year" -> "YEAR(create_time) = YEAR(NOW())";
            default -> "DATE(create_time) = CURDATE()";
        };
        return getOrdersByCondition(condition);
    }

    private List<OrderInfo> getOrdersByCondition(String condition) {
        List<OrderInfo> result = new ArrayList<>();
        
        List<OrderInfo> pending = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .apply(condition)
                        .in(OrderInfo::getStatus, 0, 1, 3)
                        .orderByAsc(OrderInfo::getCreateTime)
        );
        
        List<OrderInfo> completed = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .apply(condition)
                        .eq(OrderInfo::getStatus, 2)
                        .orderByDesc(OrderInfo::getCreateTime)
        );
        
        result.addAll(pending);
        result.addAll(completed);
        
        for (OrderInfo order : result) {
            List<OrderItem> items = orderItemMapper.selectList(
                    new LambdaQueryWrapper<OrderItem>()
                            .eq(OrderItem::getOrderId, order.getId())
            );
            order.setItems(items);
        }
        
        return result;
    }

    public void updateStatus(Long id, Integer status) {
        if (status == null || status < 0 || status > 3) {
            throw new RuntimeException("无效的订单状态");
        }
        OrderInfo order = orderInfoMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        order.setStatus(status);
        orderInfoMapper.updateById(order);
    }

    public Page<OrderInfo> search(int page, int size, String orderNo) {
        Page<OrderInfo> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(OrderInfo::getOrderNo, orderNo);
        }
        wrapper.orderByDesc(OrderInfo::getCreateTime);
        return orderInfoMapper.selectPage(pageObj, wrapper);
    }

    public OrderDetailVO getDetail(Long id) {
        OrderInfo order = orderInfoMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        OrderDetailVO vo = new OrderDetailVO();
        vo.setId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setTotalAmount(order.getTotalAmount());
        vo.setRemark(order.getRemark());
        vo.setStatus(order.getStatus());
        vo.setCreateTime(order.getCreateTime());

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, id)
        );

        List<OrderDetailVO.OrderItemVO> itemVOs = items.stream().map(item -> {
            OrderDetailVO.OrderItemVO itemVO = new OrderDetailVO.OrderItemVO();
            itemVO.setProductName(item.getProductName());
            itemVO.setPrice(item.getPrice());
            itemVO.setQuantity(item.getQuantity());
            return itemVO;
        }).collect(Collectors.toList());

        vo.setItems(itemVOs);
        return vo;
    }

    @Transactional(rollbackFor = Exception.class)
    public String createManualOrder(ManualOrderDTO dto) {
        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("订单商品不能为空");
        }

        List<Long> productIds = dto.getItems().stream()
                .map(ManualOrderDTO.OrderItemDTO::getProductId)
                .collect(Collectors.toList());

        List<Product> productList = productMapper.selectList(
                new LambdaQueryWrapper<Product>().in(Product::getId, productIds)
        );
        Map<Long, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (ManualOrderDTO.OrderItemDTO itemDto : dto.getItems()) {
            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                throw new RuntimeException("商品数量无效");
            }
            Product product = productMap.get(itemDto.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new RuntimeException("商品不存在或已下架");
            }

            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(itemDto.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(itemDto.getQuantity());
            orderItems.add(orderItem);
        }

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(orderNoGenerator.generate());
        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setRemark(dto.getRemark());
        orderInfo.setStatus(0);

        orderInfoMapper.insert(orderInfo);

        for (OrderItem item : orderItems) {
            item.setOrderId(orderInfo.getId());
            orderItemMapper.insert(item);
        }

        return orderInfo.getOrderNo();
    }
}
