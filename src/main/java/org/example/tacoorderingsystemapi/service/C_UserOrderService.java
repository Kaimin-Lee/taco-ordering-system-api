package org.example.tacoorderingsystemapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.tacoorderingsystemapi.entity.OrderInfo;
import org.example.tacoorderingsystemapi.entity.OrderItem;
import org.example.tacoorderingsystemapi.mapper.OrderInfoMapper;
import org.example.tacoorderingsystemapi.mapper.OrderItemMapper;
import org.example.tacoorderingsystemapi.util.UserContext;
import org.example.tacoorderingsystemapi.vo.OrderDetailVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class C_UserOrderService {

    private static final Logger log = LoggerFactory.getLogger(C_UserOrderService.class);

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    public List<OrderDetailVO> getMyOrders() {
        Long userId = UserContext.getUserId();
        log.info("获取用户订单列表, userId: {}", userId);
        
        List<OrderInfo> orders = orderInfoMapper.selectList(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getUserId, userId)
                        .orderByDesc(OrderInfo::getCreateTime)
        );
        
        log.info("找到 {} 条订单", orders.size());

        return orders.stream().map(order -> {
            OrderDetailVO vo = new OrderDetailVO();
            vo.setId(order.getId());
            vo.setOrderNo(order.getOrderNo());
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

    public OrderDetailVO getOrderDetail(String orderNo) {
        Long userId = UserContext.getUserId();
        log.info("获取订单详情, userId: {}, orderNo: {}", userId, orderNo);
        
        OrderInfo order = orderInfoMapper.selectOne(
                new LambdaQueryWrapper<OrderInfo>()
                        .eq(OrderInfo::getUserId, userId)
                        .eq(OrderInfo::getOrderNo, orderNo)
        );

        if (order == null) {
            log.warn("订单不存在: {}", orderNo);
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
    }
}
