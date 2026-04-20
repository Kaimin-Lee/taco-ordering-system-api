package org.example.tacoorderingsystemapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.tacoorderingsystemapi.entity.OrderInfo;
import org.example.tacoorderingsystemapi.entity.OrderItem;
import org.example.tacoorderingsystemapi.mapper.OrderInfoMapper;
import org.example.tacoorderingsystemapi.mapper.OrderItemMapper;
import org.example.tacoorderingsystemapi.util.UserContext;
import org.example.tacoorderingsystemapi.vo.OrderDetailVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class C_UserOrderService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    public List<OrderDetailVO> getMyOrders() {
        Long userId = UserContext.getUserId();
        List<OrderInfo> orders = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getUserId, userId)
                        .orderByDesc(OrderInfo::getCreateTime)
        );

        return orders.stream().map(order -> {
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
                            .eq(OrderItem::getOrderId, order.getId())
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
        }).collect(Collectors.toList());
    }
}
