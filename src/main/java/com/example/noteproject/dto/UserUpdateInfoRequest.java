package com.example.noteproject.dto;

import lombok.Data;

/**
 * 用户基本信息修改请求参数（用户名 + 手机号）。
 */
@Data
public class UserUpdateInfoRequest {

    /**
     * APP 端页面输入的用户名（必传，可能与原值相同）。
     */
    private String username;

    /**
     * APP 端页面输入的手机号（必传，可能与原值相同）。
     */
    private String phone;
}

