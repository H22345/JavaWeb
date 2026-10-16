package SHmarketplace.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import SHmarketplace.common.Result;
import SHmarketplace.exception.ErrorCode;
import SHmarketplace.util.JwtUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * JWT 认证过滤器
 * <p>
 * 工作链路：
 * <pre>
 * 请求 → CorsFilter → JwtAuthFilter → SecurityFilterChain → Controller
 *                         ↓
 *                 ① 白名单路径直接放行
 *                 ② 无 Authorization 头 → 放行（Security 后续会判断是否需要认证）
 *                 ③ Token 非法/过期 → 返回 401 JSON
 *                 ④ Token 有效 → 解析 userId，写入 SecurityContextHolder，放行
 * </pre>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. 取 Authorization 头
        String authHeader = request.getHeader("Authorization");

        // 2. 没有 Token → 直接放行（白名单路径 Security 会 permitAll；受保护路径 Security 会返回 401）
        if (authHeader == null || authHeader.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 提取纯 Token
        String token = jwtUtil.extractToken(authHeader);
        if (token == null) {
            // Header 有但格式不对（不是 Bearer 开头）
            writeUnauthorized(response, ErrorCode.UNAUTHORIZED, "Token 格式错误");
            return;
        }

        // 4. 校验 + 解析
        Long userId;
        String username;
        try {
            userId = jwtUtil.getUserIdFromToken(token);
            username = jwtUtil.getUsernameFromToken(token);
        } catch (Exception e) {
            log.warn("JWT 解析失败: {}", e.getMessage());
            writeUnauthorized(response, ErrorCode.UNAUTHORIZED, "Token 已失效或非法");
            return;
        }

        // 5. 构造 Authentication 写入 SecurityContext
        // principal = userId（Long），credentials = null，authorities = 给个默认的 ROLE_USER
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userId,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        authentication.setDetails(Map.of("username", username));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.debug("JWT 认证成功: userId={}, username={}", userId, username);

        // 6. 放行
        filterChain.doFilter(request, response);
    }

    /**
     * 返回 401 JSON 响应
     */
    private void writeUnauthorized(HttpServletResponse response, ErrorCode code, String msg) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> body = Result.error(code.getCode(), msg);
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}