package com.example.noteproject.service.impl;

import com.example.noteproject.dto.bill.BillBudgetGetResponse;
import com.example.noteproject.dto.bill.BillBudgetSetRequest;
import com.example.noteproject.entity.BillBudget;
import com.example.noteproject.entity.BillCategory;
import com.example.noteproject.entity.BillRecord;
import com.example.noteproject.exception.BusinessException;
import com.example.noteproject.repository.BillBudgetRepository;
import com.example.noteproject.repository.BillCategoryRepository;
import com.example.noteproject.repository.BillRecordRepository;
import com.example.noteproject.service.BillBudgetService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 月度预算服务实现。设置/修改月度预算（userId+yearMonth 唯一）；查询预算及超支情况。
 */
@Service
public class BillBudgetServiceImpl implements BillBudgetService {

    private static final DateTimeFormatter YEAR_MONTH = DateTimeFormatter.ofPattern("yyyy-MM");
    private static final int TYPE_SPEND = 2;

    private final BillBudgetRepository budgetRepository;
    private final BillCategoryRepository categoryRepository;
    private final BillRecordRepository recordRepository;

    public BillBudgetServiceImpl(BillBudgetRepository budgetRepository,
                                 BillCategoryRepository categoryRepository,
                                 BillRecordRepository recordRepository) {
        this.budgetRepository = budgetRepository;
        this.categoryRepository = categoryRepository;
        this.recordRepository = recordRepository;
    }

    @Override
    @Transactional
    public void set(Integer userId, BillBudgetSetRequest request) {
        validateUserId(userId);
        if (request == null) {
            throw new BusinessException("请求体不能为空");
        }
        String yearMonth = validateYearMonth(request.getYearMonth());
        BigDecimal budgetAmount = validateBudgetAmount(request.getBudgetAmount());

        BillBudget budget = budgetRepository.findByUserIdAndYearMonth(userId, yearMonth).orElse(null);
        if (budget == null) {
            budget = new BillBudget();
            budget.setUserId(userId);
            budget.setYearMonth(yearMonth);
        }
        budget.setBudgetAmount(budgetAmount);
        budgetRepository.save(budget);
    }

    @Override
    public BillBudgetGetResponse get(Integer userId, String yearMonth) {
        validateUserId(userId);
        String ym = yearMonth != null && !yearMonth.trim().isEmpty()
                ? validateYearMonth(yearMonth.trim())
                : YearMonth.now().format(YEAR_MONTH);

        BigDecimal budgetAmount = budgetRepository.findByUserIdAndYearMonth(userId, ym)
                .map(BillBudget::getBudgetAmount)
                .orElse(BigDecimal.ZERO);

        YearMonth ymParsed = YearMonth.parse(ym, YEAR_MONTH);
        LocalDateTime start = ymParsed.atDay(1).atStartOfDay();
        LocalDateTime end = ymParsed.atEndOfMonth().atTime(23, 59, 59, 999_999_999);

        List<Integer> categoryIds = categoryRepository.findByUserIdOrderByCreateTimeAsc(userId)
                .stream().map(BillCategory::getId).collect(java.util.stream.Collectors.toList());
        if (categoryIds.isEmpty()) {
            BillBudgetGetResponse r = new BillBudgetGetResponse();
            r.setYearMonth(ym);
            r.setBudgetAmount(budgetAmount);
            r.setTotalSpend(BigDecimal.ZERO);
            r.setRemainAmount(budgetAmount);
            r.setIsOverspend(false);
            r.setOverspendAmount(BigDecimal.ZERO);
            return r;
        }

        List<BillRecord> records = recordRepository.findByCategoryIdInAndCreateTimeBetween(categoryIds, start, end);
        BigDecimal totalSpend = records.stream()
                .filter(r -> r.getType() == TYPE_SPEND)
                .map(BillRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainAmount = budgetAmount.subtract(totalSpend);
        if (remainAmount.compareTo(BigDecimal.ZERO) < 0) {
            remainAmount = BigDecimal.ZERO;
        }
        boolean isOverspend = totalSpend.compareTo(budgetAmount) > 0;
        BigDecimal overspendAmount = BigDecimal.ZERO;
        if (isOverspend) {
            overspendAmount = totalSpend.subtract(budgetAmount);
        }

        BillBudgetGetResponse r = new BillBudgetGetResponse();
        r.setYearMonth(ym);
        r.setBudgetAmount(budgetAmount);
        r.setTotalSpend(totalSpend);
        r.setRemainAmount(remainAmount);
        r.setIsOverspend(isOverspend);
        r.setOverspendAmount(overspendAmount);
        return r;
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
    }

    private String validateYearMonth(String yearMonth) {
        if (yearMonth == null || yearMonth.trim().isEmpty()) {
            throw new BusinessException("年月不能为空");
        }
        try {
            YearMonth.parse(yearMonth.trim(), YEAR_MONTH);
            return yearMonth.trim();
        } catch (Exception e) {
            throw new BusinessException("年月格式错误，应为 yyyy-MM");
        }
    }

    private BigDecimal validateBudgetAmount(BigDecimal amount) {
        if (amount == null) {
            throw new BusinessException("预算金额不能为空");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("预算金额必须大于0");
        }
        return amount;
    }
}
