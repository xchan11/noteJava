package com.example.noteproject.service;

import com.example.noteproject.dto.goods.GoodsAddRequest;
import com.example.noteproject.dto.goods.GoodsAddResponse;
import com.example.noteproject.dto.goods.GoodsListItem;
import com.example.noteproject.dto.goods.GoodsUpdateRequest;

import java.util.List;

public interface GoodsInfoService {

    GoodsAddResponse add(Integer userId, GoodsAddRequest request);

    GoodsAddResponse update(Integer userId, GoodsUpdateRequest request);

    void delete(Integer userId, Integer goodsId);

    List<GoodsListItem> listByCategory(Integer userId, Integer categoryId);

    /** type 7/3/1 对应 7天/3天/1天 提醒。 */
    List<GoodsListItem> listByRemind(Integer userId, Integer type);
}
