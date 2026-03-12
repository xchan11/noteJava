package com.example.noteproject.common;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 通用接口返回结果封装。统一使用 message 字段（前端约定），不使用 msg。
 *
 * @param <T> 返回数据类型
 */
public class ApiResponse<T> {

    /**
     * 状态码：200 成功，401 未登录，400 业务错误，其它按业务约定。
     */
    private int code;

    /**
     * 提示信息，面向前端和用户。序列化字段名固定为 message。
     */
    @JsonProperty("message")
    private String message;

    /**
     * 具体返回数据。
     */
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data);
    }

    /**
     * 自定义成功状态码，便于与前端约定 200 等值。
     */
    public static <T> ApiResponse<T> success(int code, String message, T data) {
        return new ApiResponse<>(code, message, data);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

