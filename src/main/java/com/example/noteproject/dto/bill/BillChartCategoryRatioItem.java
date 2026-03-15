package com.example.noteproject.dto.bill;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillChartCategoryRatioItem {
    private String categoryName;
    private BigDecimal totalAmount;
    /** 占比，保留2位小数。 */
    private BigDecimal ratio;
}
