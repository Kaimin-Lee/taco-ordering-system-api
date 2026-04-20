package org.example.tacoorderingsystemapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class OrderSubmitDTO {
    private String tableNo; // 桌号/取餐号
    private String remark;  // 忌口/备注
    private List<OrderItemDTO> items; // 购物车商品列表

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private Integer quantity;
    }
}