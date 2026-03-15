package com.example.noteproject.service.impl;

import com.example.noteproject.dto.bill.BillCategoryAddRequest;
import com.example.noteproject.dto.bill.BillCategoryItemResponse;
import com.example.noteproject.dto.bill.BillCategoryUpdateRequest;
import com.example.noteproject.entity.BillCategory;
import com.example.noteproject.exception.BusinessException;
import com.example.noteproject.repository.BillCategoryRepository;
import com.example.noteproject.repository.BillRecordRepository;
import com.example.noteproject.service.BillCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 记账分类服务实现。校验分类名称、同一用户下不重名；删除前校验该分类下无流水记录。
 */
@Service
public class BillCategoryServiceImpl implements BillCategoryService {

    private final BillCategoryRepository categoryRepository;
    private final BillRecordRepository recordRepository;

    public BillCategoryServiceImpl(BillCategoryRepository categoryRepository,
                                   BillRecordRepository recordRepository) {
        this.categoryRepository = categoryRepository;
        this.recordRepository = recordRepository;
    }

    @Override
    @Transactional
    public BillCategoryItemResponse add(Integer userId, BillCategoryAddRequest request) {
        validateUserId(userId);
        String name = normalizeCategoryName(request != null ? request.getCategoryName() : null);
        if (categoryRepository.findByUserIdAndCategoryName(userId, name).isPresent()) {
            throw new BusinessException("分类名称已存在");
        }
        BillCategory category = new BillCategory();
        category.setUserId(userId);
        category.setCategoryName(name);
        category.setCreateTime(System.currentTimeMillis());
        BillCategory saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public BillCategoryItemResponse update(Integer userId, BillCategoryUpdateRequest request) {
        validateUserId(userId);
        if (request == null || request.getCategoryId() == null) {
            throw new BusinessException("categoryId不能为空");
        }
        BillCategory category = categoryRepository.findByIdAndUserId(request.getCategoryId(), userId)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        String name = normalizeCategoryName(request.getCategoryName());
        if (categoryRepository.findByUserIdAndCategoryNameAndIdNot(userId, name, request.getCategoryId()).isPresent()) {
            throw new BusinessException("分类名称已存在");
        }
        category.setCategoryName(name);
        BillCategory saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Integer userId, Integer categoryId) {
        validateUserId(userId);
        if (categoryId == null) {
            throw new BusinessException("categoryId不能为空");
        }
        BillCategory category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        if (recordRepository.countByCategoryId(categoryId) > 0) {
            throw new BusinessException("该分类下有收支记录，无法删除");
        }
        categoryRepository.delete(category);
    }

    @Override
    public List<BillCategoryItemResponse> list(Integer userId) {
        validateUserId(userId);
        List<BillCategory> list = categoryRepository.findByUserIdOrderByCreateTimeAsc(userId);
        List<BillCategoryItemResponse> result = new ArrayList<>(list.size());
        for (BillCategory c : list) {
            result.add(toResponse(c));
        }
        return result;
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new BusinessException(401, "未登录");
        }
    }

    private String normalizeCategoryName(String categoryName) {
        if (!StringUtils.hasText(categoryName)) {
            throw new BusinessException("分类名称不能为空");
        }
        String name = categoryName.trim();
        if (name.length() > 100) {
            throw new BusinessException("分类名称长度不能超过100字");
        }
        return name;
    }

    private BillCategoryItemResponse toResponse(BillCategory c) {
        BillCategoryItemResponse r = new BillCategoryItemResponse();
        r.setCategoryId(c.getId());
        r.setCategoryName(c.getCategoryName());
        return r;
    }
}
