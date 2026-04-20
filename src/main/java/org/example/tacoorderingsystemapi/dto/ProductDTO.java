package org.example.tacoorderingsystemapi.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDTO {
    private Long categoryId;
    private String name;
    private String imageUrl;
    private BigDecimal price;
    private String description;
    private Integer status;
}
