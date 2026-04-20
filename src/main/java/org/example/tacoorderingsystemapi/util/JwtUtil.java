package org.example.tacoorderingsystemapi.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    // 实际生产环境应从 yml 配置中读取
    private static final String SECRET = "street-taco-ordering-system-very-secure-key-2026";
    private static final long EXPIRATION = 604800000L; // 7天
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    /**
     * 根据用户ID生成Token
     */
    public String createToken(Long userId) {
        return Jwts.builder()
                .setSubject("USER_AUTH")
                .claim("userId", userId)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 从Token中解析出用户ID
     */
    public Long getUserId(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("userId", Long.class);
    }
}