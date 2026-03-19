package com.example.noteproject.dto.bill;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 按分类查询收支记录返回项（与现有 BillRecord 结构保持一致）。
 */
@Data
public class BillRecordCategoryItemResponse {
    private Integer recordId;
    private Integer categoryId;
    private String categoryName;

    /** 收支类型：1=收入，2=支出 */
    private Integer type;

    private BigDecimal amount;
    private String remark;

    /** 格式：yyyy-MM-dd HH:mm:ss */
    private String createTime;
}

