package org.example.tacoorderingsystemapi.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductMenuVO {
    private Long categoryId;
    private String categoryName;
    private Integer sortOrder;
    private List<ProductItemVO> products;

    @Data
    public static class ProductItemVO {
        private Long id;
        private String name;
        private String imageUrl;
        private BigDecimal price;
        private String description;
        private Integer status;
    }
}
