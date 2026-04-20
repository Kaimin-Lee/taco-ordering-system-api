package org.example.tacoorderingsystemapi.vo;

import lombok.Data;

@Data
public class DashboardVO {
    private Long totalOrders;
    private Long totalSales;
    private String totalAmount;
}
