package org.example.tacoorderingsystemapi.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDetailVO {
    private Long id;
    private String orderNo;
    private String tableNo;
    private BigDecimal totalAmount;
    private String remark;
    private Integer status;
    private LocalDateTime createTime;
    private List<OrderItemVO> items;

    @Data
    public static class OrderItemVO {
        private String productName;
        private BigDecimal price;
        private Integer quantity;
    }
}
