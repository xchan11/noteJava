package com.example.noteproject.repository;

import com.example.noteproject.entity.GoodsInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * 物品信息数据仓库。
 */
@Repository
public interface GoodsInfoRepository extends JpaRepository<GoodsInfo, Integer> {

    /**
     * 按 userId 删除该用户的所有物品信息。
     * 说明：goods_info 表通过 category_id 关联 goods_category，因此使用子查询按 userId 删除，避免依赖 goods_info 直接存 user_id。
     *
     * @return 删除条数
     */
    @Modifying
    @Transactional
    @Query("delete from GoodsInfo g where g.categoryId in (select c.id from GoodsCategory c where c.userId = :userId)")
    int deleteByUserId(@Param("userId") Integer userId);
}

