package com.example.noteproject.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体，对应数据库表 user。
 */
@Data
@Entity
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 用户名，非空。
     */
    @Column(nullable = false, length = 50)
    private String username;

    /**
     * 登录密码，存储 MD5 加密后的值，非空。
     */
    @Column(nullable = false, length = 64)
    private String password;

    /**
     * 手机号，唯一。
     */
    @Column(nullable = false, unique = true, length = 20)
    private String phone;

    /**
     * 创建时间，自动填充。
     */
    @Column(name = "create_time", updatable = false)
    @CreationTimestamp
    private LocalDateTime createTime;

    /**
     * 更新时间，只要有字段被更新就自动维护。
     */
    @Column(name = "update_time")
    @UpdateTimestamp
    private LocalDateTime updateTime;
}

