package org.example.tacoorderingsystemapi.vo;

import lombok.Data;
import java.util.List;

@Data
public class DashboardVO {
    private Long totalOrders;
    private Long totalSales;
    private String totalAmount;
    private List<RecentOrderVO> recentOrders;
}
