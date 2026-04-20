package org.example.tacoorderingsystemapi.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.example.tacoorderingsystemapi.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 微信用户 Mapper 接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {
}