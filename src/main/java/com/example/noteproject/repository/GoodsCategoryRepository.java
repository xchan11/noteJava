package com.example.noteproject.repository;

import com.example.noteproject.entity.GoodsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * 物品分类数据仓库。
 */
@Repository
public interface GoodsCategoryRepository extends JpaRepository<GoodsCategory, Integer> {

    /**
     * 按 userId 删除该用户的所有物品分类。
     */
    @Transactional
    void deleteByUserId(Integer userId);
}

