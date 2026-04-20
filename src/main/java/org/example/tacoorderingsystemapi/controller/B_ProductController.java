package org.example.tacoorderingsystemapi.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.tacoorderingsystemapi.dto.ProductDTO;
import org.example.tacoorderingsystemapi.entity.Product;
import org.example.tacoorderingsystemapi.service.B_ProductService;
import org.example.tacoorderingsystemapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/b/product")
public class B_ProductController {

    @Autowired
    private B_ProductService productService;

    @GetMapping("/list")
    public Result<Page<Product>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId) {
        return Result.success(productService.list(page, size, categoryId));
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody ProductDTO dto) {
        productService.add(dto);
        return Result.success();
    }

    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody ProductDTO dto) {
        productService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<Void> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        productService.updateStatus(id, status);
        return Result.success();
    }
}
