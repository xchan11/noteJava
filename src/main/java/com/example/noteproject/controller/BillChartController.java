package com.example.noteproject.controller;

import com.example.noteproject.common.ApiResponse;
import com.example.noteproject.dto.bill.BillChartCategoryRatioItem;
import com.example.noteproject.dto.bill.BillChartTrendItem;
import com.example.noteproject.service.BillChartService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 记账数据可视化接口（趋势图、分类占比）。所有 /bill/chart/** 需登录。
 */
@RestController
@RequestMapping("/bill/chart")
public class BillChartController {

    private final BillChartService billChartService;

    public BillChartController(BillChartService billChartService) {
        this.billChartService = billChartService;
    }

    @GetMapping("/trend")
    public ApiResponse<List<BillChartTrendItem>> trend(
            @RequestParam(value = "timeType", required = false, defaultValue = "month") String timeType,
            @RequestParam(value = "yearMonth", required = false) String yearMonth,
            @RequestParam(value = "weekStart", required = false) String weekStart,
            @RequestParam(value = "weekEnd", required = false) String weekEnd,
            HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        List<BillChartTrendItem> data = billChartService.trend(userId, timeType, yearMonth, weekStart, weekEnd);
        return ApiResponse.success(200, "查询成功", data);
    }

    @GetMapping("/categoryRatio")
    public ApiResponse<List<BillChartCategoryRatioItem>> categoryRatio(
            @RequestParam(value = "yearMonth", required = false) String yearMonth,
            HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        List<BillChartCategoryRatioItem> data = billChartService.categoryRatio(userId, yearMonth);
        return ApiResponse.success(200, "查询成功", data);
    }
}
