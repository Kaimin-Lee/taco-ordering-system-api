package org.example.tacoorderingsystemapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.tacoorderingsystemapi.dto.OrderSubmitDTO;
import org.example.tacoorderingsystemapi.entity.OrderInfo;
import org.example.tacoorderingsystemapi.entity.OrderItem;
import org.example.tacoorderingsystemapi.entity.Product;
import org.example.tacoorderingsystemapi.mapper.OrderInfoMapper;
import org.example.tacoorderingsystemapi.mapper.OrderItemMapper;
import org.example.tacoorderingsystemapi.mapper.ProductMapper;
import org.example.tacoorderingsystemapi.util.OrderNoGenerator;
import org.example.tacoorderingsystemapi.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class C_OrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderNoGenerator orderNoGenerator;

    @Transactional(rollbackFor = Exception.class)
    public String submitOrder(OrderSubmitDTO dto) {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        if (dto.getItems() == null || dto.getItems().isEmpty()) {
            throw new RuntimeException("订单商品不能为空");
        }

        List<Long> productIds = dto.getItems().stream()
                .map(OrderSubmitDTO.OrderItemDTO::getProductId)
                .collect(Collectors.toList());

        List<Product> productList = productMapper.selectList(
                new LambdaQueryWrapper<Product>().in(Product::getId, productIds)
        );
        Map<Long, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderSubmitDTO.OrderItemDTO itemDto : dto.getItems()) {
            if (itemDto.getQuantity() == null || itemDto.getQuantity() <= 0) {
                throw new RuntimeException("商品数量无效");
            }
            Product product = productMap.get(itemDto.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new RuntimeException("商品不存在或已下架: ID " + itemDto.getProductId());
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
        orderInfo.setUserId(userId);
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