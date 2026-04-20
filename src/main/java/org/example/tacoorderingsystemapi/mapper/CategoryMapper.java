package org.example.tacoorderingsystemapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.tacoorderingsystemapi.entity.Category;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
