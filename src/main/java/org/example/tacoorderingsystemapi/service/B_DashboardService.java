package org.example.tacoorderingsystemapi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.tacoorderingsystemapi.entity.OrderInfo;
import org.example.tacoorderingsystemapi.entity.OrderItem;
import org.example.tacoorderingsystemapi.mapper.OrderInfoMapper;
import org.example.tacoorderingsystemapi.mapper.OrderItemMapper;
import org.example.tacoorderingsystemapi.vo.DashboardVO;
import org.example.tacoorderingsystemapi.vo.RecentOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class B_DashboardService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    public DashboardVO getToday() {
        DashboardVO vo = getStatsByCondition("DATE(create_time) = CURDATE()");
        vo.setRecentOrders(getRecentOrders());
        return vo;
    }

    public DashboardVO getThisWeek() {
        DashboardVO vo = getStatsByCondition("YEARWEEK(create_time, 1) = YEARWEEK(NOW(), 1)");
        vo.setRecentOrders(getRecentOrders());
        return vo;
    }

    public DashboardVO getThisMonth() {
        DashboardVO vo = getStatsByCondition("DATE_FORMAT(create_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')");
        vo.setRecentOrders(getRecentOrders());
        return vo;
    }

    public DashboardVO getThisYear() {
        DashboardVO vo = getStatsByCondition("YEAR(create_time) = YEAR(NOW())");
        vo.setRecentOrders(getRecentOrders());
        return vo;
    }

    private List<RecentOrderVO> getRecentOrders() {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.select("id", "order_no", "total_amount", "status", "create_time")
                .orderByDesc("create_time")
                .last("LIMIT 5");
        List<OrderInfo> orders = orderInfoMapper.selectList(wrapper);
        return orders.stream().map(o -> {
            RecentOrderVO vo = new RecentOrderVO();
            vo.setId(o.getId());
            vo.setOrderNo(o.getOrderNo());
            vo.setTotalAmount(o.getTotalAmount().toString());
            vo.setStatus(o.getStatus());
            vo.setCreateTime(o.getCreateTime());
            return vo;
        }).collect(Collectors.toList());
    }

    private DashboardVO getStatsByCondition(String condition) {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.select("COUNT(*) as totalOrders", "COALESCE(SUM(total_amount), 0) as totalAmount")
                .apply(condition);

        List<Map<String, Object>> results = orderInfoMapper.selectMaps(wrapper);
        Map<String, Object> result = results.isEmpty() ? Map.of() : results.get(0);

        DashboardVO vo = new DashboardVO();
        Object totalOrdersObj = result.get("totalOrders");
        vo.setTotalOrders(totalOrdersObj != null ? ((Number) totalOrdersObj).longValue() : 0L);
        Object amountObj = result.get("totalAmount");
        vo.setTotalAmount(amountObj != null ? amountObj.toString() : "0");

        // 统计该时段内所有订单的商品总销量
        List<OrderInfo> orders = orderInfoMapper.selectList(
                new QueryWrapper<OrderInfo>().select("id").apply(condition));
        if (orders.isEmpty()) {
            vo.setTotalSales(0L);
        } else {
            List<Long> orderIds = orders.stream().map(OrderInfo::getId).collect(Collectors.toList());
            QueryWrapper<OrderItem> itemWrapper = new QueryWrapper<>();
            itemWrapper.select("COALESCE(SUM(quantity), 0) as totalSales").in("order_id", orderIds);
            List<Map<String, Object>> salesResult = orderItemMapper.selectMaps(itemWrapper);
            Object salesObj = salesResult.isEmpty() ? null : salesResult.get(0).get("totalSales");
            vo.setTotalSales(salesObj != null ? ((Number) salesObj).longValue() : 0L);
        }

        return vo;
    }
}
