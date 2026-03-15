package com.example.noteproject.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * 月度预算实体，对应表 bill_budget。
 * 唯一约束：userId + yearMonth（一个用户每月仅一条预算记录）。
 */
@Data
@Entity
@Table(name = "bill_budget", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "ym"})
})
public class BillBudget {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id", nullable = false)
    private Integer userId;

    /** 年月，格式 yyyy-MM，如 2026-03。 */
    @Column(name = "ym", nullable = false, length = 7)
    private String yearMonth;

    /** 月度总预算金额。 */
    @Column(name = "budget_amount", nullable = false, precision = 18, scale = 2)
    private BigDecimal budgetAmount;
}
