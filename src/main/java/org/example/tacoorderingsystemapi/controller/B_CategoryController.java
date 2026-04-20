package org.example.tacoorderingsystemapi.controller;

import org.example.tacoorderingsystemapi.dto.CategoryDTO;
import org.example.tacoorderingsystemapi.entity.Category;
import org.example.tacoorderingsystemapi.service.B_CategoryService;
import org.example.tacoorderingsystemapi.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/b/category")
public class B_CategoryController {

    @Autowired
    private B_CategoryService categoryService;

    @GetMapping("/list")
    public Result<List<Category>> list() {
        return Result.success(categoryService.list());
    }

    @PostMapping("/add")
    public Result<Void> add(@RequestBody CategoryDTO dto) {
        categoryService.add(dto);
        return Result.success();
    }

    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody CategoryDTO dto) {
        categoryService.update(id, dto);
        return Result.success();
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        categoryService.delete(id);
        return Result.success();
    }
}
