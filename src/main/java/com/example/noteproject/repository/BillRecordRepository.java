package com.example.noteproject.repository;

import com.example.noteproject.entity.BillRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 收支记录数据仓库。
 */
@Repository
public interface BillRecordRepository extends JpaRepository<BillRecord, Integer> {

    long countByCategoryId(Integer categoryId);

    /** 按分类 id 列表查询，按 createTime 降序，支持分页（如 limit）。 */
    List<BillRecord> findByCategoryIdInOrderByCreateTimeDesc(List<Integer> categoryIds, Pageable pageable);

    /** 按分类 id 列表 + 时间范围查询，按 createTime 降序。 */
    List<BillRecord> findByCategoryIdInAndCreateTimeBetweenOrderByCreateTimeDesc(
            List<Integer> categoryIds, LocalDateTime start, LocalDateTime end);

    /** 按分类 id 列表 + 时间范围查询（用于统计、图表）。 */
    List<BillRecord> findByCategoryIdInAndCreateTimeBetween(
            List<Integer> categoryIds, LocalDateTime start, LocalDateTime end);

    /**
     * 按 userId 删除该用户的所有收支记录。
     * 说明：bill_record 表通过 category_id 关联 bill_category，因此使用子查询按 userId 删除。
     */
    @Modifying
    @Transactional
    @Query("delete from BillRecord r where r.categoryId in (select c.id from BillCategory c where c.userId = :userId)")
    int deleteByUserId(@Param("userId") Integer userId);

    /**
     * 统计当前用户记账总笔数（收入+支出）。
     * bill_record 通过 category_id 关联 bill_category，再按 user_id 过滤。
     */
    @Query("select count(r) from BillRecord r where r.categoryId in (select c.id from BillCategory c where c.userId = :userId)")
    long countByUserId(@Param("userId") Integer userId);
}

