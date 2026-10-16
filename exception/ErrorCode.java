package SHmarketplace.exception;

/**
 * 业务错误码枚举
 * 前端根据 code 判断错误类型，message 给用户展示
 */
public enum ErrorCode {

    // 参数错误 4xx
    PARAM_INVALID(400, "参数校验失败"),
    PARAM_MISSING(400, "缺少必要参数"),

    // 认证/权限 401/403
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限访问"),

    // 资源不存在 404
    USER_NOT_FOUND(404, "用户不存在"),
    GOODS_NOT_FOUND(404, "商品不存在"),
    CATEGORY_NOT_FOUND(404, "分类不存在"),
    ORDER_NOT_FOUND(404, "订单不存在"),

    // 业务冲突 409
    USERNAME_EXISTS(409, "账号已存在"),
    STOCK_NOT_ENOUGH(409, "库存不足"),
    ORDER_STATUS_INVALID(409, "订单状态不允许该操作"),
    GOODS_OFF_SHELF(409, "商品已下架"),

    // 密码错误
    PASSWORD_ERROR(401, "账号或密码错误"),
    ACCOUNT_DISABLED(403, "账号已被禁用"),

    // 系统错误 5xx
    SYSTEM_ERROR(500, "系统内部错误，请稍后重试");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}