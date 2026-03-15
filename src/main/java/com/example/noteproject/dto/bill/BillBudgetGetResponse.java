package com.example.noteproject.dto.bill;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillBudgetGetResponse {
    private String yearMonth;
    private BigDecimal budgetAmount;
    private BigDecimal totalSpend;
    private BigDecimal remainAmount;
    private Boolean isOverspend;
}
