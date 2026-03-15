package com.example.noteproject.service.impl;

import com.example.noteproject.dto.bill.BillChartCategoryRatioItem;
import com.example.noteproject.dto.bill.BillChartTrendItem;
import com.example.noteproject.entity.BillCategory;
import com.example.noteproject.entity.BillRecord;
import com.example.noteproject.exception.BusinessException;
import com.example.noteproject.repository.BillCategoryRepository;
import com.example.noteproject.repository.BillRecordRepository;
import com.example.noteproject.service.BillChartService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 记账数据可视化服务实现。按周/月趋势（按天汇总收入/支出）；按分类统计支出占比。
 */
@Service
public class BillChartServiceImpl implements BillChartService {

    private static final DateTimeFormatter YEAR_MONTH = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final int TYPE_INCOME = 1;
    private static final int TYPE_SPEND = 2;

    private final BillCategoryRepository categoryRepository;
    private final BillRecordRepository recordRepository;

    public BillChartServiceImpl(BillCategoryRepository categoryRepository,
                                BillRecordRepository recordRepository) {
        this.categoryRepository = categoryRepository;
        this.recordRepository = recordRepository;
    }

    @Override
    public List<BillChartTrendItem> trend(Integer userId, String timeType, String yearMonth, String weekStart, String weekEnd) {
        validateUserId(userId);
        LocalDateTime start;
        LocalDateTime end;
        if ("week".equalsIgnoreCase(timeType)) {
            if (weekStart == null || weekStart.trim().isEmpty() || weekEnd == null || weekEnd.trim().isEmpty()) {
                throw new BusinessException("按周查询时需传 weekStart 和 weekEnd，格式 yyyy-MM-dd");
            }
            start = LocalDate.parse(weekStart.trim(), DATE).atStartOfDay();
            end = LocalDate.parse(weekEnd.trim(), DATE).atTime(23, 59, 59, 999_999_999);
        } else {
            if (yearMonth == null || yearMonth.trim().isEmpty()) {
                throw new BusinessException("按月查询时需传 yearMonth，格式 yyyy-MM");
            }
            YearMonth ym = YearMonth.parse(yearMonth.trim(), YEAR_MONTH);
            start = ym.atDay(1).atStartOfDay();
            end = ym.atEndOfMonth().atTime(23, 59, 59, 999_999_999);
        }

        List<Integer> categoryIds = categoryRepository.findByUserIdOrderByCreateTimeAsc(userId)
                .stream().map(BillCategory::getId).collect(Collectors.toList());
        if (categoryIds.isEmpty()) {
            return fillTrendByDay(start, end, Collections.emptyList());
        }

        List<BillRecord> records = recordRepository.findByCategoryIdInAndCreateTimeBetween(categoryIds, start, end);
        return fillTrendByDay(start, end, records);
    }

    @Override
    public List<BillChartCategoryRatioItem> categoryRatio(Integer userId, String yearMonth) {
        validateUserId(userId);
        String ym = yearMonth != null && !yearMonth.trim().isEmpty()
                ? yearMonth.trim()
                : YearMonth.now().format(YEAR_MONTH);
        try {
            YearMonth.parse(ym, YEAR_MONTH);
        } catch (Exception e) {
            throw new BusinessException("年月格式错误，应为 yyyy-MM");
        }

        YearMonth ymParsed = YearMonth.parse(ym, YEAR_MONTH);
        LocalDateTime start = ymParsed.atDay(1).atStartOfDay();
        LocalDateTime end = ymParsed.atEndOfMonth().atTime(23, 59, 59, 999_999_999);

        List<Integer> categoryIds = categoryRepository.findByUserIdOrderByCreateTimeAsc(userId)
                .stream().map(BillCategory::getId).collect(Collectors.toList());
        if (categoryIds.isEmpty()) {
            return new ArrayList<>();
        }

        List<BillRecord> records = recordRepository.findByCategoryIdInAndCreateTimeBetween(categoryIds, start, end);
        List<BillRecord> spendRecords = records.stream().filter(r -> r.getType() == TYPE_SPEND).collect(Collectors.toList());
        BigDecimal totalSpend = spendRecords.stream().map(BillRecord::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalSpend.compareTo(BigDecimal.ZERO) == 0) {
            return new ArrayList<>();
        }

        Map<Integer, BigDecimal> categorySum = spendRecords.stream()
                .collect(Collectors.groupingBy(BillRecord::getCategoryId,
                        Collectors.reducing(BigDecimal.ZERO, BillRecord::getAmount, BigDecimal::add)));
        Map<Integer, String> categoryNameMap = categoryRepository.findByUserIdOrderByCreateTimeAsc(userId)
                .stream().collect(Collectors.toMap(BillCategory::getId, BillCategory::getCategoryName));

        List<BillChartCategoryRatioItem> result = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> e : categorySum.entrySet()) {
            BillChartCategoryRatioItem item = new BillChartCategoryRatioItem();
            item.setCategoryName(categoryNameMap.getOrDefault(e.getKey(), ""));
            item.setTotalAmount(e.getValue());
            item.setRatio(e.getValue().divide(totalSpend, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP));
            result.add(item);
        }
        return result;
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
    }

    /** 按天汇总收入/支出，并补全范围内每一天（无记录则为 0）。 */
    private List<BillChartTrendItem> fillTrendByDay(LocalDateTime start, LocalDateTime end, List<BillRecord> records) {
        Map<LocalDate, BigDecimal> incomeByDay = new HashMap<>();
        Map<LocalDate, BigDecimal> spendByDay = new HashMap<>();
        for (BillRecord r : records) {
            LocalDate d = r.getCreateTime() != null ? r.getCreateTime().toLocalDate() : null;
            if (d == null) continue;
            if (r.getType() == TYPE_INCOME) {
                incomeByDay.merge(d, r.getAmount(), BigDecimal::add);
            } else if (r.getType() == TYPE_SPEND) {
                spendByDay.merge(d, r.getAmount(), BigDecimal::add);
            }
        }

        List<BillChartTrendItem> result = new ArrayList<>();
        LocalDate d = start.toLocalDate();
        LocalDate endDate = end.toLocalDate();
        while (!d.isAfter(endDate)) {
            BillChartTrendItem item = new BillChartTrendItem();
            item.setDate(d.format(DATE));
            item.setIncome(incomeByDay.getOrDefault(d, BigDecimal.ZERO));
            item.setSpend(spendByDay.getOrDefault(d, BigDecimal.ZERO));
            result.add(item);
            d = d.plusDays(1);
        }
        return result;
    }
}
