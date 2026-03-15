package com.example.noteproject.service;

import com.example.noteproject.dto.bill.BillRecordAddRequest;
import com.example.noteproject.dto.bill.BillRecordAddResponse;
import com.example.noteproject.dto.bill.BillRecordItemResponse;
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
}
