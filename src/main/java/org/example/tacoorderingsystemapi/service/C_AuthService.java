package org.example.tacoorderingsystemapi.service;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.example.tacoorderingsystemapi.entity.SysUser;
import org.example.tacoorderingsystemapi.mapper.SysUserMapper;
import org.example.tacoorderingsystemapi.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class C_AuthService {

    private static final Logger log = LoggerFactory.getLogger(C_AuthService.class);

    @Value("${wechat.miniapp.appid}")
    private String appId;

    @Value("${wechat.miniapp.secret}")
    private String secret;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private JwtUtil jwtUtil;

    private static final String WX_LOGIN_URL = "https://api.weixin.qq.com/sns/jscode2session";

    public String login(String code, String nickname, String phone) {
        log.info("开始微信登录, code: {}, nickname: {}, phone: {}", code, nickname, phone);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appid", appId);
        paramMap.put("secret", secret);
        paramMap.put("js_code", code);
        paramMap.put("grant_type", "authorization_code");

        String response = HttpUtil.get(WX_LOGIN_URL, paramMap);
        log.info("微信接口返回: {}", response);

        JSONObject jsonObject = JSONUtil.parseObj(response);
        String openId = jsonObject.getStr("openid");
        String sessionKey = jsonObject.getStr("session_key");

        if (openId == null) {
            String errMsg = jsonObject.getStr("errmsg");
            log.error("微信登录失败: {}", errMsg);
            throw new RuntimeException("微信登录失败: " + errMsg);
        }

        log.info("获取到openId: {}, sessionKey: {}", openId, sessionKey);

        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getOpenId, openId);
        SysUser user = sysUserMapper.selectOne(queryWrapper);
        log.info("用户查询完成, userId: {}, nickname: {}", user != null ? user.getId() : "null", user != null ? user.getNickname() : "null");

        if (user == null) {
            user = new SysUser();
            user.setOpenId(openId);
            user.setSessionKey(sessionKey);
            user.setNickname(nickname != null && !nickname.isEmpty() ? nickname : "塔可食客_" + IdUtil.fastSimpleUUID().substring(0, 4));
            if (phone != null && !phone.isEmpty()) {
                user.setPhone(phone);
            }
            sysUserMapper.insert(user);
            log.info("新用户创建成功, userId: {}", user.getId());
        } else {
            boolean updated = false;
            if (sessionKey != null && !sessionKey.equals(user.getSessionKey())) {
                user.setSessionKey(sessionKey);
                updated = true;
            }
            if (nickname != null && !nickname.isEmpty() && !nickname.equals(user.getNickname())) {
                user.setNickname(nickname);
                updated = true;
            }
            if (phone != null && !phone.isEmpty() && !phone.equals(user.getPhone())) {
                user.setPhone(phone);
                updated = true;
            }
            if (updated) {
                sysUserMapper.updateById(user);
                log.info("用户信息更新成功, userId: {}", user.getId());
            }
        }

        String token = jwtUtil.createToken(user.getId());
        log.info("登录成功, userId: {}, token长度: {}", user.getId(), token != null ? token.length() : 0);
        return token;
    }
}
