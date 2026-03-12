package com.example.noteproject.repository;

import com.example.noteproject.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

/**
 * 记事数据仓库。
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {

    /**
     * 按 userId 删除该用户的所有记事数据。
     */
    @Transactional
    void deleteByUserId(Integer userId);
}

