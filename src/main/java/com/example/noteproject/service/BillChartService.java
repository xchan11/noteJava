package com.example.noteproject.service;

import com.example.noteproject.dto.bill.BillChartCategoryRatioItem;
import com.example.noteproject.dto.bill.BillChartTrendItem;

import java.util.List;

/**
 * 记账数据可视化（趋势、分类占比）服务。
 */
public interface BillChartService {

    /**
     * 按周或按月查询收支趋势（按天汇总）。
     *
     * @param timeType month 或 week
     * @param yearMonth 当 timeType=month 时必填，格式 yyyy-MM
     * @param weekStart 当 timeType=week 时必填，格式 yyyy-MM-dd
     * @param weekEnd   当 timeType=week 时必填，格式 yyyy-MM-dd
     */
    List<BillChartTrendItem> trend(Integer userId, String timeType, String yearMonth, String weekStart, String weekEnd);

    /**
     * 按类别统计支出占比（某月）。
     *
     * @param yearMonth 年月，格式 yyyy-MM，默认当前月
     */
    List<BillChartCategoryRatioItem> categoryRatio(Integer userId, String yearMonth);
}
