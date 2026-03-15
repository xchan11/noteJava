package com.example.noteproject.service;

import com.example.noteproject.dto.bill.BillBudgetGetResponse;
import com.example.noteproject.dto.bill.BillBudgetSetRequest;

/**
 * 月度预算服务。
 */
public interface BillBudgetService {

    void set(Integer userId, BillBudgetSetRequest request);

    BillBudgetGetResponse get(Integer userId, String yearMonth);
}
