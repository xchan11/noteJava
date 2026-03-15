package com.example.noteproject.service;

import com.example.noteproject.dto.bill.BillCategoryAddRequest;
import com.example.noteproject.dto.bill.BillCategoryItemResponse;
import com.example.noteproject.dto.bill.BillCategoryUpdateRequest;

import java.util.List;

/**
 * 记账分类服务。
 */
public interface BillCategoryService {

    BillCategoryItemResponse add(Integer userId, BillCategoryAddRequest request);

    BillCategoryItemResponse update(Integer userId, BillCategoryUpdateRequest request);

    void delete(Integer userId, Integer categoryId);

    List<BillCategoryItemResponse> list(Integer userId);
}
