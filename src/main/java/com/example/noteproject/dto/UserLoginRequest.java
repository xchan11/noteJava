package com.example.noteproject.dto;

import lombok.Data;

/**
 * 用户登录请求参数。
 */
@Data
public class UserLoginRequest {

    private String phone;

    private String password;
}

