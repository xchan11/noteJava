package com.example.noteproject.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 物品分类实体，对应表 goods_category。
 */
@Data
@Entity
@Table(name = "goods_category")
public class GoodsCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;
}

