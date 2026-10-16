package SHmarketplace.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * <p>
 * 职责：生成 Token / 解析 Token / 校验 Token
 * <p>
 * 依赖配置（application.properties）：
 * <pre>
 * jwt.secret=xxxxx (Base64, ≥ 32 字节)
 * jwt.expire=604800  (秒)
 * </pre>
 */
@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expire:604800}")
    private long expire;

    /** 默认 Header 前缀 */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 内部：获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 JWT Token
     *
     * @param userId   用户ID（放 subject）
     * @param username 用户名（放 claims）
     * @return Bearer + JWT 字符串
     */
    public String generateToken(Long userId, String username) {
        long now = System.currentTimeMillis();
        long exp = now + expire * 1000;

        Map<String, Object> claims = new HashMap<>();
        claims.put("uid", userId);
        claims.put("uname", username);

        String token = Jwts.builder()
                .claims(claims)
                .subject(String.valueOf(userId))      // subject = 用户ID
                .issuedAt(new Date(now))               // 签发时间
                .expiration(new Date(exp))             // 过期时间
                .signWith(getSigningKey())             // HS256 签名
                .compact();

        log.debug("生成 JWT: userId={}, token={}", userId, token);
        return TOKEN_PREFIX + token;
    }

    /**
     * 从完整 Authorization 头中提取纯 Token
     *
     * @param authHeader "Bearer eyJhbG..."
     * @return "eyJhbG..."  或 null（格式不对）
     */
    public String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
            return authHeader.substring(TOKEN_PREFIX.length()).trim();
        }
        return null;
    }

    /**
     * 解析 Token，返回 Claims
     *
     * @return Claims（含 uid / uname / subject 等）
     * @throws IllegalArgumentException Token 非法或过期
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 校验 Token 有效性
     *
     * @param token 纯 Token（不带 Bearer 前缀）
     * @return true = 有效；false = 非法或过期
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            log.warn("JWT 校验失败: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 从 Token 解析中取出用户ID
     */
    public Long getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        Object uid = claims.get("uid");
        if (uid instanceof Number num) {
            return num.longValue();
        }
        return Long.valueOf(claims.getSubject());
    }

    /**
     * 从 Token 解析中取出用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        Object uname = claims.get("uname");
        return uname == null ? null : uname.toString();
    }
}
