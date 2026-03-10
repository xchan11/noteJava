package com.example.noteproject.dto;

import lombok.Data;

/**
 * 用户注册请求参数。
 */
@Data
public class UserRegisterRequest {

    private String username;

    private String phone;

    private String password;

    private String confirmPassword;
}

