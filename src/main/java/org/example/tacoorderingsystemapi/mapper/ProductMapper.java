package org.example.tacoorderingsystemapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.tacoorderingsystemapi.entity.Product;

@Mapper
public interface ProductMapper extends BaseMapper<Product> {
}
