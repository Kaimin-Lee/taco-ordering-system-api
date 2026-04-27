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

    /**
     * 提交订单
     */
    @Transactional(rollbackFor = Exception.class)
    public String submitOrder(OrderSubmitDTO dto) {
        // 1. 获取当前登录用户 ID (直接从 ThreadLocal 取，非常优雅)
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new RuntimeException("用户未登录");
        }

        // 2. 提取前端传来的商品 ID 列表，去数据库查真实数据
        List<Long> productIds = dto.getItems().stream()
                .map(OrderSubmitDTO.OrderItemDTO::getProductId)
                .collect(Collectors.toList());

        List<Product> productList = productMapper.selectList(
                new LambdaQueryWrapper<Product>().in(Product::getId, productIds)
        );
        // 转为 Map 方便后续通过 ID 快速取商品信息
        Map<Long, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        // 3. 计算总价并构建明细实体
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderSubmitDTO.OrderItemDTO itemDto : dto.getItems()) {
            Product product = productMap.get(itemDto.getProductId());
            if (product == null || product.getStatus() == 0) {
                throw new RuntimeException("商品不存在或已下架: ID " + itemDto.getProductId());
            }

            // 计算金额：单价 * 数量
            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(itemDto.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            // 构建明细对象
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName()); // 保存下单时的快照名称
            orderItem.setPrice(product.getPrice());      // 保存下单时的快照价格
            orderItem.setQuantity(itemDto.getQuantity());
            orderItems.add(orderItem);
        }

        // 4. 构建并保存订单主表
        OrderInfo orderInfo = new OrderInfo();
        // 调用我们之前写好的独立流水号生成器
        orderInfo.setOrderNo(OrderNoGenerator.generateNewTradeNo());
        orderInfo.setUserId(userId);
        orderInfo.setTableNo(dto.getTableNo());
        orderInfo.setTotalAmount(totalAmount);
        orderInfo.setRemark(dto.getRemark());
        orderInfo.setStatus(0); // 0: 待处理

        orderInfoMapper.insert(orderInfo); // 插入后，MyBatis-Plus 会自动将生成的 ID 回填到 orderInfo 中

        // 5. 保存订单明细表
        for (OrderItem item : orderItems) {
            item.setOrderId(orderInfo.getId()); // 关联主表 ID
            orderItemMapper.insert(item);
        }

        // 6. 返回生成的流水号给前端
        return orderInfo.getOrderNo();
    }
}