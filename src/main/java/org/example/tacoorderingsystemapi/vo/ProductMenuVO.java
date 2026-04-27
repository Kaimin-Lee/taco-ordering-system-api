package org.example.tacoorderingsystemapi.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductMenuVO {
    @JsonProperty("id")
    private Long categoryId;

    @JsonProperty("name")
    private String categoryName;

    private Integer sortOrder;

    @JsonProperty("items")
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
