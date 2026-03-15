package com.example.noteproject.dto.bill;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BillRecordAddRequest {
    private Integer categoryId;
    /** 1=收入，2=支出。 */
    private Integer type;
    private BigDecimal amount;
    private String remark;
    /**
     * 可选，默认当前时间。
     * 前端传入格式：yyyy-MM-dd HH:mm，例如 2026-03-15 22:09。
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createTime;
}
