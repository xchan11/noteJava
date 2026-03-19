package com.example.noteproject.controller;

import com.example.noteproject.common.ApiResponse;
import com.example.noteproject.dto.bill.BillRecordCategoryPageResponse;
import com.example.noteproject.service.BillRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * API 版本：按记账分类查询该分类下所有记账记录（不分页）。
 * 复用现有鉴权：从 Session 获取 userId，仅返回当前用户数据。
 */
@RestController
@RequestMapping("/api/bill/record")
public class ApiBillRecordController {

    private final BillRecordService billRecordService;

    public ApiBillRecordController(BillRecordService billRecordService) {
        this.billRecordService = billRecordService;
    }

    /**
     * GET /api/bill/record/category/{categoryId}
     */
    @GetMapping("/category/{categoryId}")
    public ApiResponse<BillRecordCategoryPageResponse> listByCategory(
            @PathVariable("categoryId") Long categoryId,
            HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        BillRecordCategoryPageResponse data = billRecordService.listAllByCategory(userId, categoryId);
        return ApiResponse.success(200, "查询成功", data);
    }
}

