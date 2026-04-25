package org.example.tacoorderingsystemapi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.example.tacoorderingsystemapi.mapper.OrderInfoMapper;
import org.example.tacoorderingsystemapi.entity.OrderInfo;
import org.example.tacoorderingsystemapi.vo.DashboardVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class B_DashboardService {

    @Autowired
    private OrderInfoMapper orderInfoMapper;

    public DashboardVO getToday() {
        return getStatsByCondition("DATE(create_time) = CURDATE()");
    }

    public DashboardVO getThisWeek() {
        return getStatsByCondition("YEARWEEK(create_time) = YEARWEEK(NOW())");
    }

    public DashboardVO getThisMonth() {
        return getStatsByCondition("DATE_FORMAT(create_time, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')");
    }

    public DashboardVO getThisYear() {
        return getStatsByCondition("YEAR(create_time) = YEAR(NOW())");
    }

    private DashboardVO getStatsByCondition(String condition) {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.select("COUNT(*) as totalOrders",
                        "SUM(total_amount) as totalAmount")
                .apply(condition);

        Map<String, Object> result = orderInfoMapper.selectMaps(wrapper).get(0);

        DashboardVO vo = new DashboardVO();
        vo.setTotalOrders((Long) result.get("totalOrders"));
        BigDecimal amount = (BigDecimal) result.get("totalAmount");
        vo.setTotalAmount(amount != null ? amount.toString() : "0");
        return vo;
    }
}
