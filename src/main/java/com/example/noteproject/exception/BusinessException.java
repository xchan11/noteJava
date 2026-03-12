package com.example.noteproject.exception;

/**
 * 自定义业务异常，用于封装用户可读的错误提示。
 */
public class BusinessException extends RuntimeException {

    /**
     * 业务错误码，默认值为 400（通用业务校验错误）。
     */
    private final int code;

    public BusinessException(String message) {
        super(message);
        // 默认按 400 处理业务校验错误，例如参数不合法、手机号已注册等。
        this.code = 400;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

