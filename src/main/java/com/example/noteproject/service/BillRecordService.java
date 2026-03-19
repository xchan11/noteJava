package com.example.noteproject.service;

import com.example.noteproject.dto.bill.BillRecordAddRequest;
import com.example.noteproject.dto.bill.BillRecordAddResponse;
import com.example.noteproject.dto.bill.BillRecordItemResponse;
import com.example.noteproject.dto.bill.BillRecordCategoryPageResponse;
import com.example.noteproject.dto.bill.BillRecordMonthItemResponse;
import com.example.noteproject.dto.bill.BillRecordUpdateRequest;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 收支记录服务。
 */
public interface BillRecordService {

    BillRecordAddResponse add(Integer userId, BillRecordAddRequest request);

    BillRecordAddResponse update(Integer userId, BillRecordUpdateRequest request);

    void delete(Integer userId, Integer recordId);

    List<BillRecordItemResponse> recent(Integer userId, int limit);

    List<BillRecordItemResponse> listByTime(Integer userId, LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 按月份查询当月所有收支记录（按 createTime 降序）。
     * createTime 返回格式：yyyy-MM-dd HH:mm:ss（字符串）。
     */
    List<BillRecordMonthItemResponse> listAllByMonth(Integer userId, String yearMonth);

    /**
     * 按记账分类 ID 查询该分类下所有收支记录（按 createTime 倒序，不分页）。
     */
    BillRecordCategoryPageResponse listAllByCategory(Integer userId, Long categoryId);
}
