package com.example.noteproject.repository;

import com.example.noteproject.entity.BillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * 记账类别数据仓库。
 */
@Repository
public interface BillCategoryRepository extends JpaRepository<BillCategory, Integer> {

    List<BillCategory> findByUserIdOrderByCreateTimeAsc(Integer userId);

    Optional<BillCategory> findByIdAndUserId(Integer id, Integer userId);

    /** 同一用户下分类名是否已存在（新增时用）。 */
    Optional<BillCategory> findByUserIdAndCategoryName(Integer userId, String categoryName);

    /** 同一用户下除当前分类外是否已有该名称（编辑时用）。 */
    Optional<BillCategory> findByUserIdAndCategoryNameAndIdNot(Integer userId, String categoryName, Integer id);

    /**
     * 按 userId 删除该用户的所有记账类别。
     */
    @Transactional
    void deleteByUserId(Integer userId);
}

