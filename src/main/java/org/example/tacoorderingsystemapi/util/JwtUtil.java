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
    private static final String SECRET = "street-taco-ordering-system-very-secure-key-2026";
    private static final long EXPIRATION = 604800000L;
    private static final Key KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String createToken(Long userId) {
        return buildToken(userId, "user");
    }

    public String createAdminToken(Long adminId) {
        return buildToken(adminId, "admin");
    }

    private String buildToken(Long id, String role) {
        return Jwts.builder()
                .setSubject("AUTH")
                .claim("userId", id)
                .claim("role", role)
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(KEY, SignatureAlgorithm.HS256)
                .compact();
    }

    public Long getUserId(String token) {
        return getClaims(token).get("userId", Long.class);
    }

    public String getRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
