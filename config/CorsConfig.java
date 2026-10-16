package SHmarketplace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 全局跨域（CORS）配置
 * <p>
 * 前后端分离场景下，浏览器默认禁止跨域请求，
 * 这里配置允许所有来源（开发阶段最宽松），
 * 生产环境应收紧 allowedOrigins。
 */
@Configuration
public class CorsConfig {

    /**
     * CorsFilter — 最顶层的跨域过滤器
     * 优先级高于 Spring Security，OPTIONS 预检请求在到达 Security 之前就被放行
     */
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();

        // 允许所有来源（开发态方便，生产环境改具体域名）
        config.addAllowedOriginPattern("*");
        // 是否允许携带 Cookie（带凭证时不能用 "*"，需用 allowedOriginPatterns）
        config.setAllowCredentials(true);
        // 允许的 HTTP 方法
        config.addAllowedMethod("*");           // GET / POST / PUT / DELETE / OPTIONS ...
        // 允许的请求头
        config.addAllowedHeader("*");           // 所有 Header 都放行
        // 允许浏览器读取的响应头
        config.addExposedHeader("Authorization");
        config.addExposedHeader("Content-Disposition");
        // 预检请求缓存时间（秒），浏览器不会频繁发 OPTIONS
        config.setMaxAge(3600L);                // 1 小时

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 对所有路径生效
        source.registerCorsConfiguration("/**", config);

        return new CorsFilter(source);
    }
}