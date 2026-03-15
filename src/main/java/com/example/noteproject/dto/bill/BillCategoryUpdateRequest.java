package com.example.noteproject.dto.bill;

import lombok.Data;

@Data
public class BillCategoryUpdateRequest {
    private Integer categoryId;
    private String categoryName;
}
