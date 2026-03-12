package com.example.noteproject.controller;

import com.example.noteproject.common.ApiResponse;
import com.example.noteproject.dto.UserInfoResponse;
import com.example.noteproject.dto.UserLoginRequest;
import com.example.noteproject.dto.UserRegisterRequest;
import com.example.noteproject.dto.UserUpdateInfoRequest;
import com.example.noteproject.dto.UserUpdatePwdRequest;
import com.example.noteproject.entity.User;
import com.example.noteproject.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 用户相关接口 Controller。
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册接口。
     * POST /user/register
     */
    @PostMapping("/register")
    public ApiResponse<UserInfoResponse> register(@RequestBody UserRegisterRequest request) {
        User user = userService.register(request.getUsername(), request.getPhone(),
                request.getPassword(), request.getConfirmPassword());
        // 注册成功后返回非敏感用户信息（不包含密码）
        UserInfoResponse response = new UserInfoResponse();
        response.setUserId(user.getId());
        response.setUsername(user.getUsername());
        response.setPhone(user.getPhone());
        return ApiResponse.success(200, "注册成功", response);
    }

    /**
     * 用户登录接口。
     * POST /user/login
     */
    @PostMapping("/login")
    public ApiResponse<UserInfoResponse> login(@RequestBody UserLoginRequest request, HttpServletRequest httpRequest) {
        User user = userService.login(request.getPhone(), request.getPassword());

        // 登录成功后创建/获取 Session，并写入 userId，Spring Boot 会自动通过 Set-Cookie 下发 JSESSIONID
        HttpSession session = httpRequest.getSession(true);
        session.setAttribute("userId", user.getId());

        UserInfoResponse response = new UserInfoResponse();
        BeanUtils.copyProperties(user, response);
        response.setUserId(user.getId());
        return ApiResponse.success(200, "登录成功", response);
    }

    /**
     * 根据用户 ID 查询信息。
     * GET /user/{id}
     */
    @GetMapping("/{id}")
    public ApiResponse<UserInfoResponse> getUserById(@PathVariable("id") Integer id) {
        User user = userService.getById(id);
        UserInfoResponse response = new UserInfoResponse();
        BeanUtils.copyProperties(user, response);
        response.setUserId(user.getId());
        return ApiResponse.success(200, "查询成功", response);
    }

    /**
     * 修改用户基本信息（用户名 + 手机号），按需更新（基于 Session 获取 userId）。
     * PUT /user/update-info
     */
    @PutMapping("/update-info")
    public ApiResponse<Void> updateUserInfo(@RequestBody UserUpdateInfoRequest request,
                                            HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        boolean updated = userService.updateBasicInfo(userId, request.getUsername(), request.getPhone());
        if (updated) {
            // 有字段被实际修改
            return ApiResponse.success(200, "基本信息修改成功", null);
        } else {
            // 传入的数据与数据库完全一致，无需更新
            return ApiResponse.success(200, "暂无信息需要修改", null);
        }
    }

    /**
     * 修改用户密码（基于 Session 获取 userId）。
     * PUT /user/update-pwd
     */
    @PutMapping("/update-pwd")
    public ApiResponse<Void> updatePassword(@RequestBody UserUpdatePwdRequest request,
                                            HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        userService.updatePassword(userId, request.getOldPwd(), request.getNewPwd(), request.getConfirmPwd());
        return ApiResponse.success(200, "密码修改成功", null);
    }

    /**
     * 退出登录：仅销毁当前 Session，不修改数据库。
     * DELETE /user/logout
     */
    @DeleteMapping("/logout")
    public ApiResponse<Void> logout(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ApiResponse.success(200, "退出登录成功", null);
    }

    /**
     * 注销用户：物理删除用户及其关联数据，并销毁 Session。
     * DELETE /user/cancel
     */
    @DeleteMapping("/cancel")
    public ApiResponse<Void> cancel(HttpServletRequest httpRequest) {
        HttpSession session = httpRequest.getSession(false);
        Integer userId = (session == null) ? null : (Integer) session.getAttribute("userId");
        // 物理删除 + 级联删除：删除用户及其所有关联数据，释放手机号
        userService.cancelAccount(userId);
        if (session != null) {
            session.invalidate();
        }
        return ApiResponse.success(200, "账号注销成功，数据已彻底删除", null);
    }
}

