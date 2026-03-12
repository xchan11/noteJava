package com.example.noteproject.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 收支记录实体，对应表 bill_record。
 */
@Data
@Entity
@Table(name = "bill_record")
public class BillRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "category_id", nullable = false)
    private Integer categoryId;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(length = 255)
    private String remark;

    @Column(name = "create_time")
    private LocalDateTime createTime;
}

