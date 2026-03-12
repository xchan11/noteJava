package com.example.noteproject.dto;

import lombok.Data;

/**
 * 对外返回的用户信息（隐藏密码）。
 */
@Data
public class UserInfoResponse {

    /**
     * 用户 ID（与 Android 端字段命名对齐）。
     */
    private Integer userId;

    private String username;

    private String phone;
}

