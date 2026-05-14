package org.example.tacoorderingsystemapi.controller;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.example.tacoorderingsystemapi.dto.UserUpdateDTO;
import org.example.tacoorderingsystemapi.entity.SysUser;
import org.example.tacoorderingsystemapi.mapper.SysUserMapper;
import org.example.tacoorderingsystemapi.vo.Result;
import org.example.tacoorderingsystemapi.util.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@RestController
@RequestMapping("/api/c/user")
public class C_UserController {

    @Autowired
    private SysUserMapper sysUserMapper;

    @GetMapping("/info")
    public Result<SysUser> getUserInfo() {
        Long userId = UserContext.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        return Result.success(user);
    }

    @PutMapping("/info")
    public Result<Void> updateUserInfo(@RequestBody UserUpdateDTO dto) {
        Long userId = UserContext.getUserId();
        SysUser user = sysUserMapper.selectById(userId);
        
        if (dto.getNickname() != null && !dto.getNickname().isEmpty()) {
            user.setNickname(dto.getNickname());
        }
        
        if (dto.getPhone() != null && !dto.getPhone().isEmpty()) {
            if (dto.getIv() != null && !dto.getIv().isEmpty() && user.getSessionKey() != null) {
                String decryptedPhone = decryptPhone(dto.getPhone(), dto.getIv(), user.getSessionKey());
                if (decryptedPhone != null) {
                    user.setPhone(decryptedPhone);
                } else {
                    user.setPhone("解密失败");
                }
            } else {
                user.setPhone(dto.getPhone());
            }
        }
        
        sysUserMapper.updateById(user);
        return Result.success();
    }

    private String decryptPhone(String encryptedData, String iv, String sessionKey) {
        try {
            byte[] key = Base64.getDecoder().decode(sessionKey);
            byte[] data = Base64.getDecoder().decode(encryptedData);
            byte[] ivBytes = Base64.getDecoder().decode(iv);

            SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

            byte[] decrypted = cipher.doFinal(data);
            String result = new String(decrypted, "UTF-8");
            
            JSONObject resultObj = JSONUtil.parseObj(result);
            return resultObj.getStr("phoneNumber");

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}