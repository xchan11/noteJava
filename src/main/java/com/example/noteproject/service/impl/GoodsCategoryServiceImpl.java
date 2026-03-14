package com.example.noteproject.service.impl;

import com.example.noteproject.dto.category.CategoryAddRequest;
import com.example.noteproject.dto.category.CategoryItemResponse;
import com.example.noteproject.dto.category.CategoryUpdateRequest;
import com.example.noteproject.entity.GoodsCategory;
import com.example.noteproject.exception.BusinessException;
import com.example.noteproject.repository.GoodsCategoryRepository;
import com.example.noteproject.repository.GoodsInfoRepository;
import com.example.noteproject.service.GoodsCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class GoodsCategoryServiceImpl implements GoodsCategoryService {

    private final GoodsCategoryRepository categoryRepository;
    private final GoodsInfoRepository goodsInfoRepository;

    public GoodsCategoryServiceImpl(GoodsCategoryRepository categoryRepository,
                                    GoodsInfoRepository goodsInfoRepository) {
        this.categoryRepository = categoryRepository;
        this.goodsInfoRepository = goodsInfoRepository;
    }

    @Override
    @Transactional
    public CategoryItemResponse add(Integer userId, CategoryAddRequest request) {
        validateUserId(userId);
        String name = normalizeCategoryName(request);
        if (categoryRepository.findByUserIdAndCategoryName(userId, name).isPresent()) {
            throw new BusinessException("分类名称已存在");
        }
        GoodsCategory category = new GoodsCategory();
        category.setUserId(userId);
        category.setCategoryName(name);
        category.setCreateTime(System.currentTimeMillis());
        GoodsCategory saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public CategoryItemResponse update(Integer userId, CategoryUpdateRequest request) {
        validateUserId(userId);
        if (request == null || request.getCategoryId() == null) {
            throw new BusinessException("categoryId不能为空");
        }
        GoodsCategory category = categoryRepository.findByIdAndUserId(request.getCategoryId(), userId)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        String name = normalizeCategoryName(request.getCategoryName());
        if (categoryRepository.findByUserIdAndCategoryNameAndIdNot(userId, name, request.getCategoryId()).isPresent()) {
            throw new BusinessException("分类名称已存在");
        }
        category.setCategoryName(name);
        GoodsCategory saved = categoryRepository.save(category);
        return toResponse(saved);
    }

    @Override
    public void delete(Integer userId, Integer categoryId) {
        // 删除分类及其旗下所有物品，要求整体原子性：要么都成功，要么都回滚。
        // 事务粒度放在 Service 层，避免部分删除成功、部分失败的脏数据。
        validateUserId(userId);
        if (categoryId == null) {
            throw new BusinessException("categoryId不能为空");
        }
        try {
            GoodsCategory category = categoryRepository.findByIdAndUserId(categoryId, userId)
                    .orElseThrow(() -> new BusinessException("分类不存在"));
            // 先查询该分类下的所有物品，再批量物理删除，最后删除分类本身。
            goodsInfoRepository.deleteAll(goodsInfoRepository.findByCategoryIdOrderByShelfLifeAsc(categoryId));
            categoryRepository.delete(category);
        } catch (BusinessException e) {
            // 业务异常按原有语义抛出（如分类不存在等），由全局异常处理器返回 400。
            throw e;
        } catch (Exception e) {
            // 其他异常统一包装为 500，提示用户稍后重试，事务自动回滚。
            throw new BusinessException(500, "删除失败，请重试");
        }
    }

    @Override
    public List<CategoryItemResponse> list(Integer userId) {
        validateUserId(userId);
        List<GoodsCategory> list = categoryRepository.findByUserIdOrderByCreateTimeAsc(userId);
        List<CategoryItemResponse> result = new ArrayList<>(list.size());
        for (GoodsCategory c : list) {
            result.add(toResponse(c));
        }
        return result;
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new BusinessException(400, "未登录");
        }
    }

    private String normalizeCategoryName(CategoryAddRequest request) {
        if (request == null || !StringUtils.hasText(request.getCategoryName())) {
            throw new BusinessException("分类名称不能为空");
        }
        String name = request.getCategoryName().trim();
        if (name.length() < 1 || name.length() > 20) {
            throw new BusinessException("分类名称长度为1-20字");
        }
        return name;
    }

    private String normalizeCategoryName(String categoryName) {
        if (!StringUtils.hasText(categoryName)) {
            throw new BusinessException("分类名称不能为空");
        }
        String name = categoryName.trim();
        if (name.length() < 1 || name.length() > 20) {
            throw new BusinessException("分类名称长度为1-20字");
        }
        return name;
    }

    private CategoryItemResponse toResponse(GoodsCategory c) {
        CategoryItemResponse r = new CategoryItemResponse();
        r.setCategoryId(c.getId());
        r.setCategoryName(c.getCategoryName());
        return r;
    }
}
