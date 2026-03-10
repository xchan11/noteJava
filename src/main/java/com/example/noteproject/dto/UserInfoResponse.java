package com.example.noteproject.dto;

import lombok.Data;

/**
 * 对外返回的用户信息（隐藏密码）。
 */
@Data
public class UserInfoResponse {

    private Integer id;

    private String username;

    private String phone;
}

