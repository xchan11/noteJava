package com.example.noteproject.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

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

    @Column(name = "plan_time")
    private LocalDateTime planTime;

    private Integer priority;

    @Column(name = "remind_time")
    private LocalDateTime remindTime;
}

