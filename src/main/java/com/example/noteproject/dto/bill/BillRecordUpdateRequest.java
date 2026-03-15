package com.example.noteproject.dto.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillRecordUpdateRequest {
    private Integer recordId;
    private Integer categoryId;
    private Integer type;
    private BigDecimal amount;
    private String remark;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;
}
