package org.example.tacoorderingsystemapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.example.tacoorderingsystemapi.entity.OrderInfo;

import java.util.List;

@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {

    @Select("SELECT COUNT(*) FROM order_info WHERE DATE(create_time) = CURDATE()")
    Long countTodayOrders();

    @Select("SELECT order_no FROM order_info WHERE DATE(create_time) = CURDATE() ORDER BY create_time")
    List<String> listTodayOrderNos();
}
