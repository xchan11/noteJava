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
    /** 超支金额（仅当 isOverspend=true 时 >0，否则为 0）。 */
    private BigDecimal overspendAmount;
}
