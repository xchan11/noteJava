package com.example.noteproject.service;

import com.example.noteproject.dto.category.CategoryAddRequest;
import com.example.noteproject.dto.category.CategoryItemResponse;
import com.example.noteproject.dto.category.CategoryUpdateRequest;

import java.util.List;

public interface GoodsCategoryService {

    CategoryItemResponse add(Integer userId, CategoryAddRequest request);

    CategoryItemResponse update(Integer userId, CategoryUpdateRequest request);

    void delete(Integer userId, Integer categoryId);

    List<CategoryItemResponse> list(Integer userId);
}
