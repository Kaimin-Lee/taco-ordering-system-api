package org.example.tacoorderingsystemapi.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.tacoorderingsystemapi.dto.CategoryDTO;
import org.example.tacoorderingsystemapi.entity.Category;
import org.example.tacoorderingsystemapi.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class B_CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> list() {
        return categoryMapper.selectList(
                new LambdaQueryWrapper<Category>()
                        .orderByAsc(Category::getSortOrder)
        );
    }

    public void add(CategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        category.setSortOrder(dto.getSortOrder());
        categoryMapper.insert(category);
    }

    public void update(Long id, CategoryDTO dto) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new RuntimeException("分类不存在");
        }
        category.setName(dto.getName());
        category.setSortOrder(dto.getSortOrder());
        categoryMapper.updateById(category);
    }

    public void delete(Long id) {
        categoryMapper.deleteById(id);
    }
}
