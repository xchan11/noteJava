package com.example.noteproject.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 日常记事实体，对应表 note。
 */
@Data
@Entity
@Table(name = "note")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(length = 255)
    private String title;

    @Lob
    private String content;

    /**
     * 计划时间：毫秒级时间戳（Long），数据库使用 BIGINT 存储。
     */
    @Column(name = "plan_time", nullable = false, columnDefinition = "BIGINT")
    private Long planTime;

    private Integer priority;

    /**
     * 提醒时间：毫秒级时间戳（Long），允许为空，数据库使用 BIGINT 存储。
     */
    @Column(name = "remind_time", columnDefinition = "BIGINT")
    private Long remindTime;

    /**
     * 日程状态：0 未完成 / 1 已完成。
     */
    @Column(nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer status = 0;
}

