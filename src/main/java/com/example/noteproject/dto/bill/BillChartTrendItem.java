package com.example.noteproject.dto.bill;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillChartTrendItem {
    /** 日期，格式 yyyy-MM-dd。 */
    private String date;
    private BigDecimal income;
    private BigDecimal spend;
}
