package com.example.noteproject.dto.bill;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillBudgetSetRequest {
    /** 年月，格式 yyyy-MM，必填。 */
    private String yearMonth;
    private BigDecimal budgetAmount;
}
