package SHmarketplace.resolver;

import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import SHmarketplace.annotation.CurrentUser;

/**
 * @CurrentUser 注解参数解析器
 * <p>
 * 从 SecurityContextHolder 中取出 JwtAuthFilter 写入的 userId，
 * 根据 Controller 方法参数的类型自动转换。
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        // 1. 从 SecurityContext 取 Authentication
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;   // JwtAuthFilter 已经拦了无 Token 的请求，这里理论上不会走到
        }

        // 2. JwtAuthFilter 把 userId 放在 principal 里（以 String 形式）
        Object principal = auth.getPrincipal();
        Long userId;
        if (principal instanceof Long l) {
            userId = l;
        } else {
            userId = Long.valueOf(principal.toString());
        }

        // 3. 根据 Controller 参数声明的类型做转换
        Class<?> paramType = parameter.getParameterType();
        if (paramType == Long.class || paramType == long.class) {
            return userId;
        } else if (paramType == Integer.class || paramType == int.class) {
            return userId.intValue();
        } else if (paramType == String.class) {
            return userId.toString();
        }

        // 不支持的类型（给个清晰的报错提示）
        throw new IllegalStateException(
                "@CurrentUser 注解只支持 Long / Integer / String 参数类型，当前不支持: " + paramType.getName()
        );
    }
}

