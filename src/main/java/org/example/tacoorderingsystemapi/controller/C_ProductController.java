package org.example.tacoorderingsystemapi.controller;

import org.example.tacoorderingsystemapi.service.C_ProductService;
import org.example.tacoorderingsystemapi.vo.ProductMenuVO;
import org.example.tacoorderingsystemapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/c/product")
public class C_ProductController {

    @Autowired
    private C_ProductService productService;

    @GetMapping("/menu")
    public Result<List<ProductMenuVO>> getMenu() {
        return Result.success(productService.getMenu());
    }
}
