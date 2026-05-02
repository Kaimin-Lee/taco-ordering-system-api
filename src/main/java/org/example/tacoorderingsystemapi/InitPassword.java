package org.example.tacoorderingsystemapi;

import cn.hutool.crypto.digest.BCrypt;

public class InitPassword {
    public static void main(String[] args) {
        // 你想要的明文密码
        String plaintext = "123456";

        // 生成密文
        String hashed = BCrypt.hashpw(plaintext, BCrypt.gensalt());

        System.out.println("你的明文密码是: " + plaintext);
        System.out.println("请将这串密文复制到数据库中: " + hashed);

        // 验证一下是否匹配 (模拟登录校验)
        boolean isMatch = BCrypt.checkpw(plaintext, hashed);
        System.out.println("密码校验测试: " + (isMatch ? "成功" : "失败"));
    }
}
