package org.example.tacoorderingsystemapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.tacoorderingsystemapi.entity.Category;
import org.example.tacoorderingsystemapi.entity.Product;
import org.example.tacoorderingsystemapi.mapper.CategoryMapper;
import org.example.tacoorderingsystemapi.mapper.ProductMapper;
import org.example.tacoorderingsystemapi.vo.ProductMenuVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class C_ProductService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ProductMapper productMapper;

    public List<ProductMenuVO> getMenu() {
        List<Category> categories = categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getSortOrder)
        );

        List<Product> products = productMapper.selectList(
                new LambdaQueryWrapper<Product>()
                        .eq(Product::getStatus, 1)
        );

        Map<Long, List<Product>> productMap = products.stream()
                .collect(Collectors.groupingBy(Product::getCategoryId));

        List<ProductMenuVO> result = new ArrayList<>();
        for (Category category : categories) {
            ProductMenuVO menuVO = new ProductMenuVO();
            menuVO.setCategoryId(category.getId());
            menuVO.setCategoryName(category.getName());
            menuVO.setSortOrder(category.getSortOrder());

            List<Product> categoryProducts = productMap.get(category.getId());
            if (categoryProducts != null) {
                List<ProductMenuVO.ProductItemVO> items = categoryProducts.stream()
                        .map(p -> {
                            ProductMenuVO.ProductItemVO item = new ProductMenuVO.ProductItemVO();
                            item.setId(p.getId());
                            item.setName(p.getName());
                            item.setImageUrl(p.getImageUrl());
                            item.setPrice(p.getPrice());
                            item.setDescription(p.getDescription());
                            item.setStatus(p.getStatus());
                            return item;
                        }).collect(Collectors.toList());
                menuVO.setProducts(items);
            }
            result.add(menuVO);
        }
        return result;
    }
}
