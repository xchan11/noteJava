package com.example.noteproject.repository;

import com.example.noteproject.entity.BillRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * 收支记录数据仓库。
 */
@Repository
public interface BillRecordRepository extends JpaRepository<BillRecord, Integer> {

    /**
     * 按 userId 删除该用户的所有收支记录。
     * 说明：bill_record 表通过 category_id 关联 bill_category，因此使用子查询按 userId 删除，避免依赖 bill_record 直接存 user_id。
     *
     * @return 删除条数
     */
    @Modifying
    @Transactional
    @Query("delete from BillRecord r where r.categoryId in (select c.id from BillCategory c where c.userId = :userId)")
    int deleteByUserId(@Param("userId") Integer userId);
}

