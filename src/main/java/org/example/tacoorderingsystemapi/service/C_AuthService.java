package org.example.tacoorderingsystemapi.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.tacoorderingsystemapi.entity.SysUser;
import org.example.tacoorderingsystemapi.mapper.SysUserMapper;
import org.example.tacoorderingsystemapi.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * C端用户鉴权服务
 */
@Service
public class C_AuthService {

    @Value("${wechat.miniapp.appid}")
    private String appId;

    @Value("${wechat.miniapp.secret}")
    private String secret;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    // 微信 code2Session 接口地址
    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    /**
     * 微信静默登录核心逻辑
     * * @param code 小程序端 wx.login 获取的临时登录凭证
     * @return 签发的真实 JWT Token
     */
    public String login(String code) {
        // 1. 组装请求参数调用微信接口
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appid", appId);
        paramMap.put("secret", secret);
        paramMap.put("js_code", code);
        paramMap.put("grant_type", "authorization_code");

        // 发送 HTTP GET 请求到微信服务器
        String response = HttpUtil.get(WX_LOGIN_URL, paramMap);
        JSONObject jsonObject = JSONUtil.parseObj(response);

        // 2. 解析 OpenID
        String openId = jsonObject.getStr("openid");
        if (openId == null) {
            // 记录日志或抛出自定义业务异常
            throw new RuntimeException("微信登录失败: " + jsonObject.getStr("errmsg"));
        }

        // 3. 数据库查询或注册
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getOpenId, openId);
        SysUser user = sysUserMapper.selectOne(queryWrapper);

        if (user == null) {
            // 查无此人，走新用户自动注册逻辑
            user = new SysUser();
            user.setOpenId(openId);
            // 生成默认昵称，例如: 塔可食客_a1b2
            user.setNickname("塔可食客_" + IdUtil.fastSimpleUUID().substring(0, 4));
            sysUserMapper.insert(user); // MyBatis-Plus 会自动将生成的 ID 回填到 user 对象中
        }

        // 4. 使用之前写好的 JwtUtil，根据用户的真实 ID 签发 Token
        return jwtUtil.createToken(user.getId());
    }
}