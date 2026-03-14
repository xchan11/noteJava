package com.example.noteproject.entity;

import lombok.Data;

import javax.persistence.*;

/**
 * 物品信息实体，对应表 goods_info。
 * 时间字段统一为 Long 毫秒时间戳（与 Note 模块一致）。
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

    /** 保质期截止时间，毫秒时间戳，可选（如工具类物品可不填）。 */
    @Column(name = "shelf_life", columnDefinition = "BIGINT")
    private Long shelfLife;

    /** 开封日期，毫秒时间戳，可选。 */
    @Column(name = "open_date", columnDefinition = "BIGINT")
    private Long openDate;

    /** 放置位置/溯源信息，必填。 */
    @Lob
    @Column(name = "trace_info", nullable = false)
    private String traceInfo;

    /** 距离过期 7 天的提醒时间点，自动计算。 */
    @Column(name = "remind_7d", columnDefinition = "BIGINT")
    private Long remind7d;

    @Column(name = "remind_3d", columnDefinition = "BIGINT")
    private Long remind3d;

    @Column(name = "remind_1d", columnDefinition = "BIGINT")
    private Long remind1d;

    /** 0=未过期，1=已过期，默认 0。 */
    @Column(name = "is_expire", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer isExpire = 0;
}

