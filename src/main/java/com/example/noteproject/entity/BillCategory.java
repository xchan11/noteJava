package com.example.noteproject.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 记账类别实体，对应表 bill_category。
 */
@Data
@Entity
@Table(name = "bill_category")
public class BillCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;

    /** 创建时间，毫秒时间戳，用于列表按创建时间升序。 */
    @Column(name = "create_time")
    private Long createTime;
}

