package com.example.noteproject.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 物品信息实体，对应表 goods_info。
 */
@Data
@Entity
@Table(name = "goods_info")
public class GoodsInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(name = "goods_name", nullable = false, length = 255)
    private String goodsName;

    @Column(name = "shelf_life")
    private LocalDateTime shelfLife;

    @Column(name = "open_date")
    private LocalDateTime openDate;

    @Lob
    @Column(name = "trace_info")
    private String traceInfo;

    @Column(name = "remind_7d")
    private LocalDateTime remind7d;

    @Column(name = "remind_3d")
    private LocalDateTime remind3d;

    @Column(name = "remind_1d")
    private LocalDateTime remind1d;

    @Column(name = "is_expire")
    private Integer isExpire;
}

