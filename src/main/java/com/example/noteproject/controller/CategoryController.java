package com.example.noteproject.controller;

import com.example.noteproject.common.ApiResponse;
import com.example.noteproject.dto.category.CategoryAddRequest;
import com.example.noteproject.dto.category.CategoryItemResponse;
import com.example.noteproject.dto.category.CategoryUpdateRequest;
import com.example.noteproject.service.GoodsCategoryService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 物品分类接口。所有 /category/** 需登录，从 Session 获取 userId。
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    private final GoodsCategoryService categoryService;

    public CategoryController(GoodsCategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping("/add")
    public ApiResponse<CategoryItemResponse> add(@RequestBody CategoryAddRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        CategoryItemResponse data = categoryService.add(userId, request);
        return ApiResponse.success(200, "添加成功", data);
    }

    @PutMapping("/update")
    public ApiResponse<CategoryItemResponse> update(@RequestBody CategoryUpdateRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        CategoryItemResponse data = categoryService.update(userId, request);
        return ApiResponse.success(200, "编辑成功", data);
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam("categoryId") Integer categoryId, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        categoryService.delete(userId, categoryId);
        return ApiResponse.success(200, "分类及旗下物品已删除", null);
    }

    @GetMapping("/list")
    public ApiResponse<List<CategoryItemResponse>> list(HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        List<CategoryItemResponse> data = categoryService.list(userId);
        return ApiResponse.success(200, "查询成功", data);
    }
}
