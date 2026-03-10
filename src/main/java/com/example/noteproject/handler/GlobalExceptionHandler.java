package com.example.noteproject.handler;

import com.example.noteproject.common.ApiResponse;
import com.example.noteproject.exception.BusinessException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理，统一返回前端可理解的错误信息。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常，返回友好的错误提示。
     */
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Void> handleBusinessException(BusinessException ex) {
        return ApiResponse.error(ex.getCode(), ex.getMessage());
    }

    /**
     * 兜底异常处理，避免异常信息直接暴露给前端。
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception ex) {
        // 实际项目中可以增加日志记录
        return ApiResponse.error(1, "系统异常，请稍后重试");
    }
}

