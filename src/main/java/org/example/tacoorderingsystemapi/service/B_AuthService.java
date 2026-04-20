package org.example.tacoorderingsystemapi.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.tacoorderingsystemapi.entity.SysAdmin;
import org.example.tacoorderingsystemapi.mapper.SysAdminMapper;
import org.example.tacoorderingsystemapi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class B_AuthService {

    @Autowired
    private SysAdminMapper adminMapper;

    @Autowired
    private JwtUtil jwtUtil;

    public String login(String username, String password) {
        SysAdmin admin = adminMapper.selectOne(
                new LambdaQueryWrapper<SysAdmin>()
                        .eq(SysAdmin::getUsername, username)
        );

        if (admin == null) {
            throw new RuntimeException("用户不存在");
        }

        if (!BCrypt.checkpw(password, admin.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        if (admin.getStatus() == 0) {
            throw new RuntimeException("账号已被禁用");
        }

        return jwtUtil.createToken(admin.getId());
    }
}
