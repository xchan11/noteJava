package com.example.noteproject.service;

import com.example.noteproject.entity.User;

/**
 * 用户相关业务接口。
 */
public interface UserService {

    /**
     * 用户注册。
     *
     * @param username        用户名
     * @param phone           手机号
     * @param password        密码
     * @param confirmPassword 确认密码
     * @return 注册成功后的用户实体
     */
    User register(String username, String phone, String password, String confirmPassword);

    /**
     * 用户登录。
     *
     * @param phone    手机号
     * @param password 密码
     * @return 登录成功后的用户实体
     */
    User login(String phone, String password);

    /**
     * 根据 ID 查询用户信息。
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    User getById(Integer id);
}

