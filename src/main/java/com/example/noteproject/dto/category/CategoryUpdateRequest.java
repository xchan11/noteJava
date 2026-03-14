package com.example.noteproject.dto.category;

import lombok.Data;

@Data
public class CategoryUpdateRequest {
    private Integer categoryId;
    private String categoryName;
}
