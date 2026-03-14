package com.example.noteproject.controller;

import com.example.noteproject.common.ApiResponse;
import com.example.noteproject.dto.goods.GoodsAddRequest;
import com.example.noteproject.dto.goods.GoodsAddResponse;
import com.example.noteproject.dto.goods.GoodsListItem;
import com.example.noteproject.dto.goods.GoodsUpdateRequest;
import com.example.noteproject.service.GoodsInfoService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 物品信息接口。所有 /goods/** 需登录，从 Session 获取 userId。
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    private final GoodsInfoService goodsInfoService;

    public GoodsController(GoodsInfoService goodsInfoService) {
        this.goodsInfoService = goodsInfoService;
    }

    @PostMapping("/add")
    public ApiResponse<GoodsAddResponse> add(@RequestBody GoodsAddRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        GoodsAddResponse data = goodsInfoService.add(userId, request);
        return ApiResponse.success(200, "添加成功", data);
    }

    @PutMapping("/update")
    public ApiResponse<GoodsAddResponse> update(@RequestBody GoodsUpdateRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        GoodsAddResponse data = goodsInfoService.update(userId, request);
        return ApiResponse.success(200, "编辑成功", data);
    }

    @DeleteMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam("goodsId") Integer goodsId, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        goodsInfoService.delete(userId, goodsId);
        return ApiResponse.success(200, "删除成功", null);
    }

    @GetMapping("/listByCategory")
    public ApiResponse<List<GoodsListItem>> listByCategory(@RequestParam("categoryId") Integer categoryId,
                                                           HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        List<GoodsListItem> data = goodsInfoService.listByCategory(userId, categoryId);
        return ApiResponse.success(200, "查询成功", data);
    }

    @GetMapping("/listByRemind")
    public ApiResponse<List<GoodsListItem>> listByRemind(@RequestParam("type") Integer type,
                                                         HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        List<GoodsListItem> data = goodsInfoService.listByRemind(userId, type);
        return ApiResponse.success(200, "查询成功", data);
    }
}
