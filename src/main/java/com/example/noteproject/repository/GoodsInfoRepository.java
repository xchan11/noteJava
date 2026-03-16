package com.example.noteproject.repository;

import com.example.noteproject.entity.GoodsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * 物品信息数据仓库。
 */
@Repository
public interface GoodsInfoRepository extends JpaRepository<GoodsInfo, Integer> {

    List<GoodsInfo> findByCategoryIdOrderByShelfLifeAsc(Integer categoryId);

    long countByCategoryId(Integer categoryId);

    /** 快过期列表：categoryId 属于用户，remind 已到点且未过期，按 shelfLife 升序。 */
    List<GoodsInfo> findByCategoryIdInAndRemind7dLessThanEqualAndIsExpireOrderByShelfLifeAsc(
            List<Integer> categoryIds, Long remind7d, Integer isExpire);
    List<GoodsInfo> findByCategoryIdInAndRemind3dLessThanEqualAndIsExpireOrderByShelfLifeAsc(
            List<Integer> categoryIds, Long remind3d, Integer isExpire);
    List<GoodsInfo> findByCategoryIdInAndRemind1dLessThanEqualAndIsExpireOrderByShelfLifeAsc(
            List<Integer> categoryIds, Long remind1d, Integer isExpire);

    /** 定时任务：将 shelfLife 已过期的物品标记为 isExpire=1。 */
    @Modifying
    @Transactional
    @Query("update GoodsInfo g set g.isExpire = 1 where g.shelfLife < :now and g.isExpire = 0")
    int markExpiredByShelfLifeBefore(@Param("now") Long now);

    @Modifying
    @Transactional
    @Query("delete from GoodsInfo g where g.categoryId in (select c.id from GoodsCategory c where c.userId = :userId)")
    int deleteByUserId(@Param("userId") Integer userId);

    /**
     * 统计当前用户物品总数（含过期）。
     * goods_info 通过 category_id 关联 goods_category，再按 user_id 过滤。
     */
    @Query("select count(g) from GoodsInfo g where g.categoryId in (select c.id from GoodsCategory c where c.userId = :userId)")
    long countByUserId(@Param("userId") Integer userId);
}

