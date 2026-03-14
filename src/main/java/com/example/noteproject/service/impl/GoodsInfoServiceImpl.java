package com.example.noteproject.service.impl;

import com.example.noteproject.dto.goods.GoodsAddRequest;
import com.example.noteproject.dto.goods.GoodsAddResponse;
import com.example.noteproject.dto.goods.GoodsListItem;
import com.example.noteproject.dto.goods.GoodsUpdateRequest;
import com.example.noteproject.entity.GoodsCategory;
import com.example.noteproject.entity.GoodsInfo;
import com.example.noteproject.exception.BusinessException;
import com.example.noteproject.repository.GoodsCategoryRepository;
import com.example.noteproject.repository.GoodsInfoRepository;
import com.example.noteproject.service.GoodsInfoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoodsInfoServiceImpl implements GoodsInfoService {

    private static final long DAY_MS = 24 * 60 * 60 * 1000L;

    private final GoodsInfoRepository goodsInfoRepository;
    private final GoodsCategoryRepository categoryRepository;

    public GoodsInfoServiceImpl(GoodsInfoRepository goodsInfoRepository,
                                GoodsCategoryRepository categoryRepository) {
        this.goodsInfoRepository = goodsInfoRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    @Transactional
    public GoodsAddResponse add(Integer userId, GoodsAddRequest request) {
        validateUserId(userId);
        Integer categoryId = request == null ? null : request.getCategoryId();
        GoodsCategory category = categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        validateGoodsName(request.getGoodsName());
        validateTraceInfo(request.getTraceInfo());
        Long shelfLife = request.getShelfLife();
        // shelfLife 可选，仅做基础时间戳合法性校验（允许早于当前时间）
        if (shelfLife != null && shelfLife < 0) {
            throw new BusinessException("保质期时间格式错误");
        }
        GoodsInfo goods = new GoodsInfo();
        goods.setCategoryId(category.getId());
        goods.setGoodsName(request.getGoodsName().trim());
        goods.setShelfLife(shelfLife);
        goods.setOpenDate(request.getOpenDate());
        goods.setTraceInfo(request.getTraceInfo().trim());
        goods.setIsExpire(0);
        if (shelfLife != null) {
            computeRemindTimes(goods, shelfLife);
        }
        GoodsInfo saved = goodsInfoRepository.save(goods);
        GoodsAddResponse r = new GoodsAddResponse();
        r.setGoodsId(saved.getId());
        r.setGoodsName(saved.getGoodsName());
        return r;
    }

    @Override
    @Transactional
    public GoodsAddResponse update(Integer userId, GoodsUpdateRequest request) {
        validateUserId(userId);
        if (request == null || request.getGoodsId() == null) {
            throw new BusinessException("goodsId不能为空");
        }
        GoodsInfo goods = goodsInfoRepository.findById(request.getGoodsId())
                .orElseThrow(() -> new BusinessException("物品不存在"));
        GoodsCategory category = categoryRepository.findByIdAndUserId(goods.getCategoryId(), userId)
                .orElseThrow(() -> new BusinessException("无权限修改该物品"));
        validateGoodsName(request.getGoodsName());
        validateTraceInfo(request.getTraceInfo());
        Long shelfLife = request.getShelfLife();
        // shelfLife 可选，仅做基础时间戳合法性校验（允许早于当前时间）
        if (shelfLife != null && shelfLife < 0) {
            throw new BusinessException("保质期时间格式错误");
        }
        if (!request.getCategoryId().equals(goods.getCategoryId())) {
            GoodsCategory newCategory = categoryRepository.findByIdAndUserId(request.getCategoryId(), userId)
                    .orElseThrow(() -> new BusinessException("分类不存在"));
            goods.setCategoryId(newCategory.getId());
        }
        goods.setGoodsName(request.getGoodsName().trim());
        goods.setShelfLife(shelfLife);
        goods.setOpenDate(request.getOpenDate());
        goods.setTraceInfo(request.getTraceInfo().trim());
        if (shelfLife != null) {
            computeRemindTimes(goods, shelfLife);
        } else {
            goods.setRemind7d(null);
            goods.setRemind3d(null);
            goods.setRemind1d(null);
        }
        GoodsInfo saved = goodsInfoRepository.save(goods);
        GoodsAddResponse r = new GoodsAddResponse();
        r.setGoodsId(saved.getId());
        r.setGoodsName(saved.getGoodsName());
        return r;
    }

    @Override
    @Transactional
    public void delete(Integer userId, Integer goodsId) {
        validateUserId(userId);
        if (goodsId == null) {
            throw new BusinessException("goodsId不能为空");
        }
        GoodsInfo goods = goodsInfoRepository.findById(goodsId)
                .orElseThrow(() -> new BusinessException("物品不存在"));
        categoryRepository.findByIdAndUserId(goods.getCategoryId(), userId)
                .orElseThrow(() -> new BusinessException("无权限删除该物品"));
        goodsInfoRepository.delete(goods);
    }

    @Override
    public List<GoodsListItem> listByCategory(Integer userId, Integer categoryId) {
        validateUserId(userId);
        if (categoryId == null) {
            throw new BusinessException("categoryId不能为空");
        }
        categoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new BusinessException("分类不存在"));
        List<GoodsInfo> list = goodsInfoRepository.findByCategoryIdOrderByShelfLifeAsc(categoryId);
        return list.stream().map(this::toListItem).collect(Collectors.toList());
    }

    @Override
    public List<GoodsListItem> listByRemind(Integer userId, Integer type) {
        validateUserId(userId);
        if (type == null || (type != 7 && type != 3 && type != 1)) {
            throw new BusinessException("type只能为7、3或1");
        }
        List<Integer> categoryIds = categoryRepository.findByUserIdOrderByCreateTimeAsc(userId)
                .stream().map(GoodsCategory::getId).collect(Collectors.toList());
        if (categoryIds.isEmpty()) {
            return new ArrayList<>();
        }
        long now = System.currentTimeMillis();
        List<GoodsInfo> list;
        if (type == 7) {
            list = goodsInfoRepository.findByCategoryIdInAndRemind7dLessThanEqualAndIsExpireOrderByShelfLifeAsc(
                    categoryIds, now, 0);
        } else if (type == 3) {
            list = goodsInfoRepository.findByCategoryIdInAndRemind3dLessThanEqualAndIsExpireOrderByShelfLifeAsc(
                    categoryIds, now, 0);
        } else {
            list = goodsInfoRepository.findByCategoryIdInAndRemind1dLessThanEqualAndIsExpireOrderByShelfLifeAsc(
                    categoryIds, now, 0);
        }
        return list.stream().map(this::toListItem).collect(Collectors.toList());
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            throw new BusinessException(400, "未登录");
        }
    }

    private void validateGoodsName(String name) {
        if (!StringUtils.hasText(name)) {
            throw new BusinessException("物品名称不能为空");
        }
        if (name.trim().length() > 255) {
            throw new BusinessException("物品名称过长");
        }
    }

    /** 放置位置必填。 */
    private void validateTraceInfo(String traceInfo) {
        if (!StringUtils.hasText(traceInfo)) {
            throw new BusinessException("放置位置不能为空");
        }
    }

    private void computeRemindTimes(GoodsInfo goods, Long shelfLife) {
        goods.setRemind7d(shelfLife - 7 * DAY_MS);
        goods.setRemind3d(shelfLife - 3 * DAY_MS);
        goods.setRemind1d(shelfLife - 1 * DAY_MS);
    }

    private GoodsListItem toListItem(GoodsInfo g) {
        GoodsListItem item = new GoodsListItem();
        item.setGoodsId(g.getId());
        item.setCategoryId(g.getCategoryId());
        item.setGoodsName(g.getGoodsName());
        item.setShelfLife(g.getShelfLife());
        item.setOpenDate(g.getOpenDate());
        item.setTraceInfo(g.getTraceInfo());
        item.setRemind7d(g.getRemind7d());
        item.setRemind3d(g.getRemind3d());
        item.setRemind1d(g.getRemind1d());
        item.setIsExpire(g.getIsExpire());
        return item;
    }
}
