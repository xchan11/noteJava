package com.example.noteproject.common;

/**
 * 通用接口返回结果封装。
 *
 * @param <T> 返回数据类型
 */
public class ApiResponse<T> {

    /**
     * 业务状态码，0 表示成功，其它表示失败。
     */
    private int code;

    /**
     * 提示信息，面向前端和用户。
     */
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
        return new ApiResponse<>(0, "success", data);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(0, message, data);
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

