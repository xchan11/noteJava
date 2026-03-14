package com.example.noteproject.dto.goods;

import lombok.Data;

@Data
public class GoodsAddRequest {
    private Integer categoryId;
    private String goodsName;
    private Long shelfLife;
    private Long openDate;
    private String traceInfo;
}
