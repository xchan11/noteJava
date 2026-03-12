package com.example.noteproject.repository;

import com.example.noteproject.entity.BillCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * 记账类别数据仓库。
 */
@Repository
public interface BillCategoryRepository extends JpaRepository<BillCategory, Integer> {

    /**
     * 按 userId 删除该用户的所有记账类别。
     */
    @Transactional
    void deleteByUserId(Integer userId);
}

