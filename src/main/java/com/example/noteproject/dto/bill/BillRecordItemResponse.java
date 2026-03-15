package com.example.noteproject.dto.bill;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillRecordItemResponse {
    private Integer recordId;
    private Integer categoryId;
    private String categoryName;
    private Integer type;
    private BigDecimal amount;
    private String remark;
    private LocalDateTime createTime;
}
