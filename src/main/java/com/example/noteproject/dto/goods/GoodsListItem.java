package com.example.noteproject.dto.goods;

import lombok.Data;

@Data
public class GoodsListItem {
    private Integer goodsId;
    private Integer categoryId;
    private String goodsName;
    private Long shelfLife;
    private Long openDate;
    private String traceInfo;
    private Long remind7d;
    private Long remind3d;
    private Long remind1d;
    private Integer isExpire;
}
