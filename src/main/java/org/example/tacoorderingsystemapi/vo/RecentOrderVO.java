package org.example.tacoorderingsystemapi.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RecentOrderVO {
    private Long id;
    private String orderNo;
    private String totalAmount;
    private Integer status;
    private LocalDateTime createTime;
}