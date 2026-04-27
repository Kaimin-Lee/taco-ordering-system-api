package org.example.tacoorderingsystemapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.tacoorderingsystemapi.entity.OrderInfo;
import org.example.tacoorderingsystemapi.entity.OrderItem;
import org.example.tacoorderingsystemapi.mapper.OrderInfoMapper;
import org.example.tacoorderingsystemapi.mapper.OrderItemMapper;
import org.example.tacoorderingsystemapi.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class B_OrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    public List<OrderInfo> getTodayOrders() {
        return orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .apply("DATE(create_time) = CURDATE()")
                        .orderByAsc(OrderInfo::getStatus)
                        .orderByDesc(OrderInfo::getCreateTime)
        );
    }

    public void updateStatus(Long id, Integer status) {
        OrderInfo order = orderInfoMapper.selectById(id);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }
        order.setStatus(status);
        orderInfoMapper.updateById(order);
    }

    public Page<OrderInfo> search(int page, int size, String orderNo, String tableNo) {
        Page<OrderInfo> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<OrderInfo> wrapper = new LambdaQueryWrapper<>();
        if (orderNo != null && !orderNo.isEmpty()) {
            wrapper.like(OrderInfo::getOrderNo, orderNo);
        }
        if (tableNo != null && !tableNo.isEmpty()) {
            wrapper.like(OrderInfo::getTableNo, tableNo);
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
        vo.setTableNo(order.getTableNo());
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
}
