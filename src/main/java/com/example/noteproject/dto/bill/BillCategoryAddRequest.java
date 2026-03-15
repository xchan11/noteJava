package com.example.noteproject.dto.bill;

import lombok.Data;

@Data
public class BillCategoryAddRequest {
    /** 分类名称，必填，1-100字。 */
    private String categoryName;
}
