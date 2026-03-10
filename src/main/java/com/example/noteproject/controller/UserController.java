package com.example.noteproject.controller;

import com.example.noteproject.common.ApiResponse;
import com.example.noteproject.dto.UserInfoResponse;
import com.example.noteproject.dto.UserLoginRequest;
import com.example.noteproject.dto.UserRegisterRequest;
import com.example.noteproject.entity.User;
import com.example.noteproject.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

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
    public ApiResponse<Void> register(@RequestBody UserRegisterRequest request) {
        userService.register(request.getUsername(), request.getPhone(),
                request.getPassword(), request.getConfirmPassword());
        return ApiResponse.success("注册成功", null);
    }

    /**
     * 用户登录接口。
     * POST /user/login
     */
    @PostMapping("/login")
    public ApiResponse<UserInfoResponse> login(@RequestBody UserLoginRequest request) {
        User user = userService.login(request.getPhone(), request.getPassword());
        UserInfoResponse response = new UserInfoResponse();
        BeanUtils.copyProperties(user, response);
        return ApiResponse.success("登录成功", response);
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
        return ApiResponse.success("查询成功", response);
    }
}

