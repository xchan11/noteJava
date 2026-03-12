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

    /**
     * 修改用户基本信息（用户名、手机号），按需更新。
     *
     * @param id       用户 ID
     * @param username 页面传入的用户名（可能与原值相同）
     * @param phone    页面传入的手机号（可能与原值相同）
     * @return 是否有字段被实际修改
     */
    boolean updateBasicInfo(Integer id, String username, String phone);

    /**
     * 修改用户密码。
     *
     * @param id         用户 ID
     * @param oldPwd     原密码
     * @param newPwd     新密码
     * @param confirmPwd 确认新密码
     */
    void updatePassword(Integer id, String oldPwd, String newPwd, String confirmPwd);

    /**
     * 注销用户（物理删除）：删除用户及其关联数据，释放手机号。
     *
     * @param id 用户 ID
     */
    void cancelAccount(Integer id);
}

