package SHmarketplace.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当前登录用户注解
 * <p>
 * 标注在 Controller 方法参数上，自动注入 SecurityContext 中的当前登录用户 ID。
 * 参数类型支持：Long / Integer / String（分别返回 userId、userId.intValue()、userId.toString()）。
 * <p>
 * 使用示例：
 * <pre>
 * public Result<Orders> placeOrder(@CurrentUser Long userId, ...) { ... }
 * </pre>
 *
 * @see SHmarketplace.resolver.CurrentUserArgumentResolver
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}