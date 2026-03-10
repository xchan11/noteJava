package com.example.noteproject.service.impl;

import com.example.noteproject.entity.User;
import com.example.noteproject.exception.BusinessException;
import com.example.noteproject.repository.UserRepository;
import com.example.noteproject.service.UserService;
import com.example.noteproject.util.MD5Util;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

/**
 * 用户业务实现类，包含注册、登录等核心逻辑。
 */
@Service
public class UserServiceImpl implements UserService {

    /**
     * 中国大陆 11 位手机号简单校验正则。
     */
    private static final Pattern PHONE_PATTERN = Pattern.compile("^1[3-9]\\d{9}$");

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(String username, String phone, String password, String confirmPassword) {
        // ① 非空校验
        if (!StringUtils.hasText(username) ||
                !StringUtils.hasText(phone) ||
                !StringUtils.hasText(password) ||
                !StringUtils.hasText(confirmPassword)) {
            throw new BusinessException("用户名/手机号/密码不能为空");
        }

        // ② 手机号格式校验
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException("手机号格式不正确（需为11位有效手机号）");
        }

        // ③ 手机号唯一性校验
        User existing = userRepository.findByPhone(phone);
        if (existing != null) {
            throw new BusinessException("该手机号已注册");
        }

        // ④ 密码一致性校验
        if (!password.equals(confirmPassword)) {
            throw new BusinessException("两次密码输入不一致");
        }

        // ⑤ 密码加密并保存
        String encryptedPassword = MD5Util.encrypt(password);
        User user = new User();
        user.setUsername(username.trim());
        user.setPhone(phone.trim());
        user.setPassword(encryptedPassword);

        return userRepository.save(user);
    }

    @Override
    public User login(String phone, String password) {
        // 非空校验（此处只需要手机号和密码）
        if (!StringUtils.hasText(phone) || !StringUtils.hasText(password)) {
            throw new BusinessException("用户名/手机号/密码不能为空");
        }

        // 手机号格式校验
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new BusinessException("手机号格式不正确（需为11位有效手机号）");
        }

        // 手机号存在性校验
        User user = userRepository.findByPhone(phone);
        if (user == null) {
            throw new BusinessException("该手机号未注册");
        }

        // 密码校验（加密后比对）
        String encryptedPassword = MD5Util.encrypt(password);
        if (!encryptedPassword.equals(user.getPassword())) {
            throw new BusinessException("密码错误");
        }

        return user;
    }

    @Override
    public User getById(Integer id) {
        if (id == null) {
            throw new BusinessException("用户 ID 不能为空");
        }
        Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElseThrow(() -> new BusinessException("用户不存在"));
    }
}

