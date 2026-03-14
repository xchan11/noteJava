package com.example.noteproject.schedule;

import com.example.noteproject.repository.GoodsInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 定时将 shelfLife 已过期的物品标记为 isExpire=1。每天凌晨 0 点执行。
 */
@Component
public class GoodsExpireSchedule {

    private static final Logger log = LoggerFactory.getLogger(GoodsExpireSchedule.class);

    private final GoodsInfoRepository goodsInfoRepository;

    public GoodsExpireSchedule(GoodsInfoRepository goodsInfoRepository) {
        this.goodsInfoRepository = goodsInfoRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void markExpired() {
        long now = System.currentTimeMillis();
        try {
            int count = goodsInfoRepository.markExpiredByShelfLifeBefore(now);
            if (count > 0) {
                log.info("goods_expire_schedule marked {} items as expired", count);
            }
        } catch (Exception e) {
            log.error("goods_expire_schedule failed", e);
        }
    }
}
