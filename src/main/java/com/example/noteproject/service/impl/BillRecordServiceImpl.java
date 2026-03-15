package com.example.noteproject.service.impl;

import com.example.noteproject.dto.bill.BillRecordAddRequest;
import com.example.noteproject.dto.bill.BillRecordAddResponse;
import com.example.noteproject.dto.bill.BillRecordItemResponse;
import com.example.noteproject.dto.bill.BillRecordUpdateRequest;
import com.example.noteproject.entity.BillCategory;
import com.example.noteproject.entity.BillRecord;
import com.example.noteproject.exception.BusinessException;
import com.example.noteproject.repository.BillCategoryRepository;
import com.example.noteproject.repository.BillRecordRepository;
import com.example.noteproject.service.BillRecordService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收支记录服务实现。校验 categoryId 归属当前用户、type 1/2、amount>0；createTime 未传默认当前时间。
 */
@Service
public class BillRecordServiceImpl implements BillRecordService {

    private static final int TYPE_INCOME = 1;
    private static final int TYPE_SPEND = 2;

    private final BillRecordRepository recordRepository;
    private final BillCategoryRepository categoryRepository;

    public BillRecordServiceImpl(BillRecordRepository recordRepository,
                                 BillCategoryRepository categoryRepository) {
        this.recordRepository = recordRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public BillRecordAddResponse add(Integer userId, BillRecordAddRequest request) {
        validateUserId(userId);
        if (request == null) {
            throw new BusinessException("请求体不能为空");
        }
        Integer categoryId = request.getCategoryId();
        BillCategory category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        validateType(request.getType());
        BigDecimal amount = validateAmount(request.getAmount());
        LocalDateTime createTime = request.getCreateTime() != null ? request.getCreateTime() : LocalDateTime.now();

        BillRecord record = new BillRecord();
        record.setCategoryId(category.getId());
        record.setType(request.getType());
        record.setAmount(amount);
        record.setRemark(request.getRemark());
        record.setCreateTime(createTime);
        BillRecord saved = recordRepository.save(record);

        BillRecordAddResponse r = new BillRecordAddResponse();
        r.setRecordId(saved.getId());
        r.setAmount(saved.getAmount());
        return r;
    }

    @Override
    @Transactional
    public BillRecordAddResponse update(Integer userId, BillRecordUpdateRequest request) {
        validateUserId(userId);
        if (request == null || request.getRecordId() == null) {
            throw new BusinessException("recordId不能为空");
        }
        BillRecord record = recordRepository.findById(request.getRecordId())
                .orElseThrow(() -> new BusinessException("收支记录不存在"));
        BillCategory category = categoryRepository.findByIdAndUserId(record.getCategoryId(), userId)
                .orElseThrow(() -> new BusinessException("无权限修改该记录"));
        Integer newCategoryId = request.getCategoryId();
        if (newCategoryId != null && !newCategoryId.equals(record.getCategoryId())) {
            BillCategory newCategory = categoryRepository.findByIdAndUserId(newCategoryId, userId)
                    .orElseThrow(() -> new BusinessException("分类不存在"));
            record.setCategoryId(newCategory.getId());
        }
        if (request.getType() != null) {
            validateType(request.getType());
            record.setType(request.getType());
        }
        if (request.getAmount() != null) {
            record.setAmount(validateAmount(request.getAmount()));
        }
        if (request.getRemark() != null) {
            record.setRemark(request.getRemark());
        }
        if (request.getCreateTime() != null) {
            record.setCreateTime(request.getCreateTime());
        }
        BillRecord saved = recordRepository.save(record);
        BillRecordAddResponse r = new BillRecordAddResponse();
        r.setRecordId(saved.getId());
        r.setAmount(saved.getAmount());
        return r;
    }

    @Override
    @Transactional
    public void delete(Integer userId, Integer recordId) {
        validateUserId(userId);
        if (recordId == null) {
            throw new BusinessException("recordId不能为空");
        }
        BillRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new BusinessException("收支记录不存在"));
        categoryRepository.findByIdAndUserId(record.getCategoryId(), userId)
                .orElseThrow(() -> new BusinessException("无权限删除该记录"));
        recordRepository.delete(record);
    }

    @Override
    public List<BillRecordItemResponse> recent(Integer userId, int limit) {
        validateUserId(userId);
        List<Integer> categoryIds = categoryRepository.findByUserIdOrderByCreateTimeAsc(userId)
                .stream().map(BillCategory::getId).collect(Collectors.toList());
        if (categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<BillRecord> list = recordRepository.findByCategoryIdInOrderByCreateTimeDesc(
                categoryIds, PageRequest.of(0, limit > 0 ? limit : 10));
        Map<Integer, String> categoryNameMap = categoryRepository.findByUserIdOrderByCreateTimeAsc(userId)
                .stream().collect(Collectors.toMap(BillCategory::getId, BillCategory::getCategoryName));
        return list.stream().map(r -> toItemResponse(r, categoryNameMap)).collect(Collectors.toList());
    }

    @Override
    public List<BillRecordItemResponse> listByTime(Integer userId, LocalDateTime startTime, LocalDateTime endTime) {
        validateUserId(userId);
        if (startTime == null || endTime == null) {
            throw new BusinessException("startTime和endTime不能为空");
        }
        List<Integer> categoryIds = categoryRepository.findByUserIdOrderByCreateTimeAsc(userId)
                .stream().map(BillCategory::getId).collect(Collectors.toList());
        if (categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        List<BillRecord> list = recordRepository.findByCategoryIdInAndCreateTimeBetweenOrderByCreateTimeDesc(
                categoryIds, startTime, endTime);
        Map<Integer, String> categoryNameMap = categoryRepository.findByUserIdOrderByCreateTimeAsc(userId)
                .stream().collect(Collectors.toMap(BillCategory::getId, BillCategory::getCategoryName));
        return list.stream().map(r -> toItemResponse(r, categoryNameMap)).collect(Collectors.toList());
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
    }

    private void validateType(Integer type) {
        if (type == null) {
            throw new BusinessException("收支类型不能为空");
        }
        if (type != TYPE_INCOME && type != TYPE_SPEND) {
            throw new BusinessException("收支类型只能为1（收入）或2（支出）");
        }
    }

    private BigDecimal validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new BusinessException("金额不能为空");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("金额必须大于0");
        }
        return amount;
    }

    private BillRecordItemResponse toItemResponse(BillRecord r, Map<Integer, String> categoryNameMap) {
        BillRecordItemResponse item = new BillRecordItemResponse();
        item.setRecordId(r.getId());
        item.setCategoryId(r.getCategoryId());
        item.setCategoryName(categoryNameMap.getOrDefault(r.getCategoryId(), ""));
        item.setType(r.getType());
        item.setAmount(r.getAmount());
        item.setRemark(r.getRemark());
        item.setCreateTime(r.getCreateTime());
        return item;
    }
}
