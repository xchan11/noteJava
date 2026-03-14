package com.example.noteproject.repository;

import com.example.noteproject.entity.GoodsCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * 物品分类数据仓库。
 */
@Repository
public interface GoodsCategoryRepository extends JpaRepository<GoodsCategory, Integer> {

    List<GoodsCategory> findByUserIdOrderByCreateTimeAsc(Integer userId);

    Optional<GoodsCategory> findByIdAndUserId(Integer id, Integer userId);

    /** 同一用户下分类名是否已存在（新增时用）。 */
    Optional<GoodsCategory> findByUserIdAndCategoryName(Integer userId, String categoryName);

    /** 同一用户下除当前分类外是否已有该名称（编辑时用）。 */
    Optional<GoodsCategory> findByUserIdAndCategoryNameAndIdNot(Integer userId, String categoryName, Integer id);

    /**
     * 按 userId 删除该用户的所有物品分类。
     */
    @Transactional
    void deleteByUserId(Integer userId);
}

