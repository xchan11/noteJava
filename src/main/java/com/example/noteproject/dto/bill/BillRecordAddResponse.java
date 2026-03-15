package com.example.noteproject.dto.bill;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BillRecordAddResponse {
    private Integer recordId;
    private BigDecimal amount;
}
