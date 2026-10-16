package SHmarketplace.config;

import SHmarketplace.common.Result;
import SHmarketplace.security.JwtAuthFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Spring Security 安全配置
 * <p>
 * 当前放行所有公开接口，Knife4j 文档和注册登录不需要认证。
 * 后续接入 JWT 后，在 requestMatchers 里放开写接口 + 加 JwtFilter 替换 UsernamePasswordAuthenticationFilter。
 */
@Configuration
public class SecurityConfig {

    /**
     * 密码加密器 — BCrypt，自动加盐、慢哈希抗暴力破解
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
 * ObjectMapper — 用于 JwtAuthFilter 返回 JSON 错误响应
 * Spring Boot 通常会自动配置，这里显式注册确保可用
 */
    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }


    /**
     * 安全过滤链
     * <p>
     * JwtAuthFilter 在 UsernamePasswordAuthenticationFilter 之前执行，
     * 解析 Authorization 头 → 写 SecurityContext → 后续请求通过 SecurityContextHolder 获取当前用户。
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthFilter jwtAuthFilter) throws Exception {
        http
            // CORS —— 委托给 CorsConfig 的 CorsFilter 处理
            .cors(withDefaults())
            // CSRF 禁用：前后端分离用 Token，不需要 Session CSRF 保护
            .csrf(AbstractHttpConfigurer::disable)
            // 请求授权规则
            .authorizeHttpRequests(auth -> auth
                // ===== 公开接口（不需要登录）=====
                .requestMatchers(
                    // ===== 静态资源（前端页面）=====
                    "/",
                    "/index.html",
                    "/assets/**",
                    "/*.js",
                    "/*.css",
                    "/*.png",
                    "/*.jpg",
                    "/*.svg",
                    "/favicon.ico",
                    // ===== Knife4j / OpenAPI 文档 =====
                    "/doc.html",
                    "/doc.html/**",
                    "/webjars/**",
                    "/v3/api-docs/**",
                    "/v3/api-docs",
                    "/swagger-ui/**",
                    "/swagger-resources/**",
                    "/error",
                    "/actuator/**"
                ).permitAll()
                // 认证相关：注册、登录
                .requestMatchers("/api/auth/register", "/api/auth/login").permitAll()
                // 商品浏览（不需要登录，但创建/上下架需要）
                .requestMatchers(
                    PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/api/goods"),
                    PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/api/goods/**")
                ).permitAll()
                // 留言浏览（查询商品留言不需要登录）
                .requestMatchers(
                    PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/api/message/goods/**")
                ).permitAll()
                // 分类浏览（首页展示分类不需要登录）
                .requestMatchers(
                    PathPatternRequestMatcher.pathPattern(HttpMethod.GET, "/api/category/**")
                ).permitAll()
                // ===== 其余所有请求都需要认证 =====
                .anyRequest().authenticated()
            )
            // JWT 过滤器 —— 在 UsernamePasswordAuthenticationFilter 之前执行
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            // 异常处理 —— 覆盖默认的 403/401 行为，统一返回 JSON
            .exceptionHandling(ex -> ex
                // 未认证（无 Token / Token 无效）→ 401
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(
                        objectMapper().writeValueAsString(
                            Result.error(401, "未登录或 Token 已过期，请重新登录")
                        )
                    );
                })
                // 已认证但无权限 → 403
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.getWriter().write(
                        objectMapper().writeValueAsString(
                            Result.error(403, "无权限访问该接口")
                        )
                    );
                })
            );
        return http.build();
    }
}