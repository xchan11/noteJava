package com.example.noteproject.controller;

import com.example.noteproject.common.ApiResponse;
import com.example.noteproject.dto.bill.BillCategoryAddRequest;
import com.example.noteproject.dto.bill.BillCategoryDeleteRequest;
import com.example.noteproject.dto.bill.BillCategoryItemResponse;
import com.example.noteproject.dto.bill.BillCategoryUpdateRequest;
import com.example.noteproject.service.BillCategoryService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 记账分类接口。所有 /bill/category/** 需登录，从 Session 获取 userId。
 */
@RestController
@RequestMapping("/bill/category")
public class BillCategoryController {

    private final BillCategoryService billCategoryService;

    public BillCategoryController(BillCategoryService billCategoryService) {
        this.billCategoryService = billCategoryService;
    }

    @PostMapping("/add")
    public ApiResponse<BillCategoryItemResponse> add(@RequestBody BillCategoryAddRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        BillCategoryItemResponse data = billCategoryService.add(userId, request);
        return ApiResponse.success(200, "分类添加成功", data);
    }

    @PutMapping("/update")
    public ApiResponse<BillCategoryItemResponse> update(@RequestBody BillCategoryUpdateRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        BillCategoryItemResponse data = billCategoryService.update(userId, request);
        return ApiResponse.success(200, "分类编辑成功", data);
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> delete(@RequestBody BillCategoryDeleteRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        Integer categoryId = request != null ? request.getCategoryId() : null;
        billCategoryService.delete(userId, categoryId);
        return ApiResponse.success(200, "分类删除成功", null);
    }

    @GetMapping("/list")
    public ApiResponse<List<BillCategoryItemResponse>> list(HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        List<BillCategoryItemResponse> data = billCategoryService.list(userId);
        return ApiResponse.success(200, "查询成功", data);
    }
}
