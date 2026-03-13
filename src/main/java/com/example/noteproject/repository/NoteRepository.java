package com.example.noteproject.repository;

import com.example.noteproject.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * 日程（记事）数据仓库。
 */
@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {

    /**
     * 根据 noteId + userId 查询，确保只能操作自己的日程。
     */
    Optional<Note> findByIdAndUserId(Integer id, Integer userId);

    /**
     * 查询当前用户所有日程，按 planTime 升序。
     */
    List<Note> findAllByUserIdOrderByPlanTimeAsc(Integer userId);

    /**
     * 按 userId 删除该用户的所有记事数据。
     */
    @Transactional
    void deleteByUserId(Integer userId);
}

