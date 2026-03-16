package com.example.noteproject.controller;

import com.example.noteproject.common.ApiResponse;
import com.example.noteproject.exception.BusinessException;
import com.example.noteproject.repository.BillRecordRepository;
import com.example.noteproject.repository.GoodsInfoRepository;
import com.example.noteproject.repository.NoteRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局统计接口：用于首页/概览展示核心数据。
 * 所有接口仅返回当前登录用户的数据，从 Session 获取 userId。
 */
@RestController
@RequestMapping("/stats")
public class StatsController {

    private final GoodsInfoRepository goodsInfoRepository;
    private final BillRecordRepository billRecordRepository;
    private final NoteRepository noteRepository;

    public StatsController(GoodsInfoRepository goodsInfoRepository,
                           BillRecordRepository billRecordRepository,
                           NoteRepository noteRepository) {
        this.goodsInfoRepository = goodsInfoRepository;
        this.billRecordRepository = billRecordRepository;
        this.noteRepository = noteRepository;
    }

    /**
     * 查询用户物品总数（含过期）。
     */
    @GetMapping("/goodsTotal")
    public ApiResponse<Map<String, Long>> goodsTotal(HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        long count = goodsInfoRepository.countByUserId(userId);
        Map<String, Long> data = new HashMap<>();
        data.put("count", count);
        return ApiResponse.success(200, "查询成功", data);
    }

    /**
     * 查询用户记账总笔数（收入+支出）。
     */
    @GetMapping("/billTotal")
    public ApiResponse<Map<String, Long>> billTotal(HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        long count = billRecordRepository.countByUserId(userId);
        Map<String, Long> data = new HashMap<>();
        data.put("count", count);
        return ApiResponse.success(200, "查询成功", data);
    }

    /**
     * 查询用户待办日程数（已完成不计）。
     * Note.status：0 未完成 / 1 已完成。
     */
    @GetMapping("/noteTodo")
    public ApiResponse<Map<String, Long>> noteTodo(HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
        long count = noteRepository.countByUserIdAndStatus(userId, 0);
        Map<String, Long> data = new HashMap<>();
        data.put("count", count);
        return ApiResponse.success(200, "查询成功", data);
    }
}

