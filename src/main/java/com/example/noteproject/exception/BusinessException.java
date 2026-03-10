package com.example.noteproject.exception;

/**
 * 自定义业务异常，用于封装用户可读的错误提示。
 */
public class BusinessException extends RuntimeException {

    /**
     * 业务错误码，默认值为 1。
     */
    private final int code;

    public BusinessException(String message) {
        super(message);
        this.code = 1;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}

