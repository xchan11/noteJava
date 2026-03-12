package com.example.noteproject.dto;

import lombok.Data;

/**
 * 用户修改密码请求参数。
 */
@Data
public class UserUpdatePwdRequest {

    private String oldPwd;

    private String newPwd;

    private String confirmPwd;
}

