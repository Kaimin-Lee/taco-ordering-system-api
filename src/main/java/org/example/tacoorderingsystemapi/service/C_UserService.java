package org.example.tacoorderingsystemapi.service;

import cn.hutool.core.bean.BeanUtil;
import org.example.tacoorderingsystemapi.dto.UpdateProfileDTO;
import org.example.tacoorderingsystemapi.entity.SysUser;
import org.example.tacoorderingsystemapi.mapper.SysUserMapper;
import org.example.tacoorderingsystemapi.util.UserContext;
import org.example.tacoorderingsystemapi.vo.UserProfileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class C_UserService {

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * 获取当前用户信息
     */
    public UserProfileVO getProfile() {
        Long userId = UserContext.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        UserProfileVO vo = new UserProfileVO();
        vo.setId(user.getId());
        vo.setNickname(user.getNickname());
        vo.setPhone(user.getPhone());
        return vo;
    }

    /**
     * 更新用户信息
     */
    public void updateProfile(UpdateProfileDTO dto) {
        Long userId = UserContext.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getPhone() != null) {
            user.setPhone(dto.getPhone());
        }

        sysUserMapper.updateById(user);
    }
}
