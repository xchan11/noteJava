package com.example.noteproject.dto.bill;

import lombok.Data;

import java.util.List;

/**
 * 按分类分页查询收支记录返回体。
 */
@Data
public class BillRecordCategoryPageResponse {
    private long total;
    private List<BillRecordCategoryItemResponse> list;
}

