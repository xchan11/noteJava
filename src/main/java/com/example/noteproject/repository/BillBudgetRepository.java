package com.example.noteproject.repository;

import com.example.noteproject.entity.BillBudget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 月度预算数据仓库。
 */
@Repository
public interface BillBudgetRepository extends JpaRepository<BillBudget, Integer> {

    Optional<BillBudget> findByUserIdAndYearMonth(Integer userId, String yearMonth);

    /** 用户注销时删除其所有预算记录。 */
    void deleteByUserId(Integer userId);
}
