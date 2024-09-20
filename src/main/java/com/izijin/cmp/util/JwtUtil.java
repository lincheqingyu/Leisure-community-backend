package com.izijin.cmp.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    private final SecretKey key;

    @Value("${jwt.expiration}")
    private Long expiration;

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        // 确保密钥长度至少为 256 位（32 字节）
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret key must be at least 256 bits long");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    // 删除未使用的secret字段
    // @Value("${jwt.secret}")
    // private String secret;

    // 生成JWT令牌
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    // 创建JWT令牌的具体实现
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)  // 设置JWT的声明（可以包含自定义信息）
                .setSubject(subject)  // 设置JWT的主题（通常是用户名）
                .setIssuedAt(new Date(System.currentTimeMillis()))  // 设置JWT的签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))  // 设置JWT的过期时间
                .signWith(key)  // 使用key字段
                .compact();  // 生成JWT字符串
    }

    // 验证JWT令牌
    public Boolean validateToken(String token, String username) {
        final String tokenUsername = extractUsername(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    // 从JWT中提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从JWT中提取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 从JWT中提取指定的声明
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 解析JWT，获取所有声明
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)  // 使用key字段
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 检查JWT是否过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
}