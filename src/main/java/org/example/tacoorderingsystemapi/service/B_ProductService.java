package org.example.tacoorderingsystemapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.tacoorderingsystemapi.dto.ProductDTO;
import org.example.tacoorderingsystemapi.entity.Product;
import org.example.tacoorderingsystemapi.mapper.ProductMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class B_ProductService {

    @Autowired
    private ProductMapper productMapper;

    public Page<Product> list(int page, int size, Long categoryId) {
        Page<Product> pageObj = new Page<>(page, size);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (categoryId != null) {
            wrapper.eq(Product::getCategoryId, categoryId);
        }
        return productMapper.selectPage(pageObj, wrapper);
    }

    public void add(ProductDTO dto) {
        Product product = new Product();
        product.setCategoryId(dto.getCategoryId());
        product.setName(dto.getName());
        product.setImageUrl(dto.getImageUrl());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setStatus(dto.getStatus());
        productMapper.insert(product);
    }

    public void update(Long id, ProductDTO dto) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        product.setCategoryId(dto.getCategoryId());
        product.setName(dto.getName());
        product.setImageUrl(dto.getImageUrl());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        product.setStatus(dto.getStatus());
        productMapper.updateById(product);
    }

    public void delete(Long id) {
        productMapper.deleteById(id);
    }

    public void updateStatus(Long id, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            throw new RuntimeException("商品不存在");
        }
        product.setStatus(status);
        productMapper.updateById(product);
    }
}
