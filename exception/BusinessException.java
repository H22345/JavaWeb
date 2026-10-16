package SHmarketplace.exception;

/**
 * 业务异常 — 交给 GlobalExceptionHandler 统一处理
 * 只在业务操作失败时抛出，不要用于纯查询查不到数据的场景
 */
public class BusinessException extends RuntimeException {

    private final int code;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.getCode();
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}