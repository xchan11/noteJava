package com.example.noteproject.controller;

import com.example.noteproject.common.ApiResponse;
import com.example.noteproject.dto.bill.BillBudgetGetResponse;
import com.example.noteproject.dto.bill.BillBudgetSetRequest;
import com.example.noteproject.service.BillBudgetService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.YearMonth;

/**
 * 月度预算接口。所有 /bill/budget/** 需登录，从 Session 获取 userId。
 */
@RestController
@RequestMapping("/bill/budget")
public class BillBudgetController {

    private final BillBudgetService billBudgetService;

    public BillBudgetController(BillBudgetService billBudgetService) {
        this.billBudgetService = billBudgetService;
    }

    @PostMapping("/set")
    public ApiResponse<BillBudgetGetResponse> set(@RequestBody BillBudgetSetRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        billBudgetService.set(userId, request);
        BillBudgetGetResponse data = new BillBudgetGetResponse();
        data.setYearMonth(request.getYearMonth());
        data.setBudgetAmount(request.getBudgetAmount());
        return ApiResponse.success(200, "预算设置成功", data);
    }

    @GetMapping("/get")
    public ApiResponse<BillBudgetGetResponse> get(
            @RequestParam(value = "yearMonth", required = false) String yearMonth,
            HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        if (yearMonth == null || yearMonth.trim().isEmpty()) {
            yearMonth = YearMonth.now().toString();
        }
        BillBudgetGetResponse data = billBudgetService.get(userId, yearMonth);
        return ApiResponse.success(200, "查询成功", data);
    }
}
