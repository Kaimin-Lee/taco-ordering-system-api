package org.example.tacoorderingsystemapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class ManualOrderDTO {
    private List<OrderItemDTO> items;
    private String remark;

    @Data
    public static class OrderItemDTO {
        private Long productId;
        private Integer quantity;
    }
}