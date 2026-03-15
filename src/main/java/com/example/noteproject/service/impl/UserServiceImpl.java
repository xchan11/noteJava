package com.example.noteproject.service.impl;

import com.example.noteproject.entity.User;
import com.example.noteproject.exception.BusinessException;
import com.example.noteproject.repository.BillBudgetRepository;
import com.example.noteproject.repository.BillCategoryRepository;
import com.example.noteproject.repository.BillRecordRepository;
import com.example.noteproject.repository.GoodsCategoryRepository;
import com.example.noteproject.repository.GoodsInfoRepository;
import com.example.noteproject.repository.NoteRepository;
import com.example.noteproject.repository.UserRepository;
import com.example.noteproject.service.UserService;
import com.example.noteproject.util.MD5Util;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

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
    private final NoteRepository noteRepository;
    private final GoodsInfoRepository goodsInfoRepository;
    private final BillRecordRepository billRecordRepository;
    private final GoodsCategoryRepository goodsCategoryRepository;
    private final BillCategoryRepository billCategoryRepository;
    private final BillBudgetRepository billBudgetRepository;

    public UserServiceImpl(UserRepository userRepository,
                           NoteRepository noteRepository,
                           GoodsInfoRepository goodsInfoRepository,
                           BillRecordRepository billRecordRepository,
                           GoodsCategoryRepository goodsCategoryRepository,
                           BillCategoryRepository billCategoryRepository,
                           BillBudgetRepository billBudgetRepository) {
        this.userRepository = userRepository;
        this.noteRepository = noteRepository;
        this.goodsInfoRepository = goodsInfoRepository;
        this.billRecordRepository = billRecordRepository;
        this.goodsCategoryRepository = goodsCategoryRepository;
        this.billCategoryRepository = billCategoryRepository;
        this.billBudgetRepository = billBudgetRepository;
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
            // 登录场景下：若账号不存在（包含已被物理注销删除的情况），统一提示为“手机号未注册”
            throw new BusinessException("手机号未注册");
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

    @Override
    public boolean updateBasicInfo(Integer id, String username, String phone) {
        if (id == null) {
            throw new BusinessException("用户 ID 不能为空");
        }

        // ① 根据 ID 查询用户是否存在
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // ③ 非空校验：用户名和手机号都不能为空白
        if (!StringUtils.hasText(username) || !StringUtils.hasText(phone)) {
            throw new BusinessException("用户名/手机号不能为空");
        }

        String trimmedUsername = username.trim();
        String trimmedPhone = phone.trim();

        // ④ 判断字段是否实际发生变化（按需更新）
        boolean usernameChanged = !trimmedUsername.equals(user.getUsername());
        boolean phoneChanged = !trimmedPhone.equals(user.getPhone());

        // 若手机号发生变更，才进行格式和唯一性校验
        if (phoneChanged) {
            // 格式校验
            if (!PHONE_PATTERN.matcher(trimmedPhone).matches()) {
                throw new BusinessException("手机号格式不正确");
            }
            // 唯一性校验（排除当前用户本身）
            User other = userRepository.findByPhoneAndIdNot(trimmedPhone, id);
            if (other != null) {
                throw new BusinessException("该手机号已注册");
            }
        }

        // 若用户名发生变化，仅需非空（已在上方统一校验），无需额外逻辑

        // 没有任何字段变化，则直接返回 false，由 Controller 决定返回文案
        if (!usernameChanged && !phoneChanged) {
            return false;
        }

        // 按需更新字段，JPA 会自动维护 update_time（@UpdateTimestamp）
        if (usernameChanged) {
            user.setUsername(trimmedUsername);
        }
        if (phoneChanged) {
            user.setPhone(trimmedPhone);
        }

        userRepository.save(user);
        return true;
    }

    @Override
    public void updatePassword(Integer id, String oldPwd, String newPwd, String confirmPwd) {
        if (id == null) {
            throw new BusinessException("用户 ID 不能为空");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        if (!StringUtils.hasText(oldPwd) || !StringUtils.hasText(newPwd) || !StringUtils.hasText(confirmPwd)) {
            throw new BusinessException("密码不能为空");
        }

        // 校验原密码
        String encryptedOldPwd = MD5Util.encrypt(oldPwd);
        if (!encryptedOldPwd.equals(user.getPassword())) {
            throw new BusinessException("原密码错误");
        }

        // 新密码一致性校验
        if (!newPwd.equals(confirmPwd)) {
            throw new BusinessException("两次密码输入不一致");
        }

        // 更新新密码（加密存储）
        user.setPassword(MD5Util.encrypt(newPwd));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void cancelAccount(Integer id) {
        if (id == null) {
            throw new BusinessException("用户不存在");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("用户不存在"));

        // 物理删除 + 级联删除（按顺序）：先删关联数据，再删用户，避免外键约束报错
        noteRepository.deleteByUserId(id);
        goodsInfoRepository.deleteByUserId(id);
        billRecordRepository.deleteByUserId(id);
        goodsCategoryRepository.deleteByUserId(id);
        billCategoryRepository.deleteByUserId(id);
        billBudgetRepository.deleteByUserId(id);

        userRepository.delete(user);
    }
}

