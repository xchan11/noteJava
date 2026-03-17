package com.example.noteproject.dto.bill;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 按月份查询收支记录返回项（createTime 格式化为 yyyy-MM-dd HH:mm:ss 字符串）。
 */
@Data
public class BillRecordMonthItemResponse {
    private Integer recordId;
    private Integer categoryId;
    private String categoryName;
    private Integer type;
    private BigDecimal amount;
    private String remark;
    private String createTime;
}

