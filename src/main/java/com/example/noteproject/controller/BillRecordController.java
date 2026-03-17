package com.example.noteproject.controller;

import com.example.noteproject.common.ApiResponse;
import com.example.noteproject.dto.bill.BillRecordAddRequest;
import com.example.noteproject.dto.bill.BillRecordAddResponse;
import com.example.noteproject.dto.bill.BillRecordItemResponse;
import com.example.noteproject.dto.bill.BillRecordMonthItemResponse;
import com.example.noteproject.dto.bill.BillRecordUpdateRequest;
import com.example.noteproject.exception.BusinessException;
import com.example.noteproject.service.BillRecordService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 收支记录接口。所有 /bill/record/** 需登录，从 Session 获取 userId。
 */
@RestController
@RequestMapping("/bill/record")
public class BillRecordController {

    private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter YEAR_MONTH = DateTimeFormatter.ofPattern("yyyy-MM");

    private final BillRecordService billRecordService;

    public BillRecordController(BillRecordService billRecordService) {
        this.billRecordService = billRecordService;
    }

    @PostMapping("/add")
    public ApiResponse<BillRecordAddResponse> add(@RequestBody BillRecordAddRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        BillRecordAddResponse data = billRecordService.add(userId, request);
        return ApiResponse.success(200, "收支记录添加成功", data);
    }

    @PutMapping("/update")
    public ApiResponse<BillRecordAddResponse> update(@RequestBody BillRecordUpdateRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        BillRecordAddResponse data = billRecordService.update(userId, request);
        return ApiResponse.success(200, "收支记录编辑成功", data);
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> delete(@RequestBody com.example.noteproject.dto.bill.BillRecordDeleteRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        Integer recordId = request != null ? request.getRecordId() : null;
        billRecordService.delete(userId, recordId);
        return ApiResponse.success(200, "收支记录删除成功", null);
    }

    @GetMapping("/recent")
    public ApiResponse<List<BillRecordItemResponse>> recent(
            @RequestParam(value = "limit", required = false, defaultValue = "10") Integer limit,
            HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        int limitVal = limit != null && limit > 0 ? limit : 10;
        List<BillRecordItemResponse> data = billRecordService.recent(userId, limitVal);
        return ApiResponse.success(200, "查询成功", data);
    }

    @GetMapping("/listByTime")
    public ApiResponse<List<BillRecordItemResponse>> listByTime(
            @RequestParam("startTime") String startTime,
            @RequestParam("endTime") String endTime,
            HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        LocalDateTime start = parseDateTime(startTime);
        LocalDateTime end = parseDateTime(endTime);
        List<BillRecordItemResponse> data = billRecordService.listByTime(userId, start, end);
        return ApiResponse.success(200, "查询成功", data);
    }

    /**
     * 按月份查询当月所有收支记录（用于月份切换的全收支列表页）。
     * yearMonth 格式：yyyy-MM；不传则默认当前月份。
     */
    @GetMapping("/listAllByMonth")
    public ApiResponse<List<BillRecordMonthItemResponse>> listAllByMonth(
            @RequestParam(value = "yearMonth", required = false) String yearMonth,
            HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        String ym = yearMonth == null || yearMonth.trim().isEmpty()
                ? YearMonth.now().format(YEAR_MONTH)
                : yearMonth.trim();
        // Controller 层做格式校验，便于返回指定提示语
        try {
            YearMonth.parse(ym, YEAR_MONTH);
        } catch (Exception e) {
            throw new BusinessException("月份格式错误，需为yyyy-MM");
        }
        List<BillRecordMonthItemResponse> data = billRecordService.listAllByMonth(userId, ym);
        if (data.isEmpty()) {
            return ApiResponse.success(200, "该月份暂无收支记录", data);
        }
        return ApiResponse.success(200, "查询成功", data);
    }

    private LocalDateTime parseDateTime(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new BusinessException("时间不能为空");
        }
        try {
            return LocalDateTime.parse(value.trim(), DATE_TIME);
        } catch (Exception e) {
            throw new BusinessException("时间格式错误，应为 yyyy-MM-dd HH:mm");
        }
    }
}
