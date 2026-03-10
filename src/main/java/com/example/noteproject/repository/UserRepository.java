package com.example.noteproject.repository;

import com.example.noteproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 用户仓库接口，提供基础的 CRUD 能力和自定义查询。
 */
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * 根据手机号查询用户，用于注册唯一性校验和登录。
     *
     * @param phone 手机号
     * @return 匹配的用户，若不存在则返回 null
     */
    User findByPhone(String phone);
}

