package com.example.noteproject.service.impl;

import com.example.noteproject.dto.note.NoteAddRequest;
import com.example.noteproject.dto.note.NoteListItem;
import com.example.noteproject.dto.note.NoteUpdateRequest;
import com.example.noteproject.entity.Note;
import com.example.noteproject.exception.BusinessException;
import com.example.noteproject.repository.NoteRepository;
import com.example.noteproject.service.NoteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 日程（Note）业务实现。
 */
@Service
public class NoteServiceImpl implements NoteService {

    private static final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);

    private final NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    @Transactional
    public NoteListItem add(Integer userId, NoteAddRequest request) {
        validateUserId(userId);
        validateAddOrUpdate(request == null ? null : request.getTitle(),
                request == null ? null : request.getPlanTime(),
                request == null ? null : request.getPriority());

        Note note = new Note();
        note.setUserId(userId);
        note.setTitle(request.getTitle().trim());
        note.setContent(request.getContent());
        note.setPlanTime(request.getPlanTime());
        note.setPriority(request.getPriority());
        note.setRemindTime(request.getRemindTime());
        note.setStatus(0);

        Note saved = noteRepository.save(note);

        return toListItem(saved);
    }

    @Override
    @Transactional
    public NoteListItem update(Integer userId, NoteUpdateRequest request) {
        validateUserId(userId);
        if (request == null || request.getNoteId() == null) {
            throw new BusinessException("noteId不能为空");
        }
        // 先校验归属：不存在/无权限分别返回具体原因
        Note existing = noteRepository.findById(request.getNoteId())
                .orElseThrow(() -> new BusinessException("日程不存在"));
        if (!userId.equals(existing.getUserId())) {
            throw new BusinessException("无权限修改该日程");
        }

        // 再校验参数合法性
        validateAddOrUpdate(request.getTitle(), request.getPlanTime(), request.getPriority());

        Note note = existing;

        note.setTitle(request.getTitle().trim());
        note.setContent(request.getContent());
        note.setPlanTime(request.getPlanTime());
        note.setPriority(request.getPriority());
        note.setRemindTime(request.getRemindTime());

        Note saved = noteRepository.save(note);

        return toListItem(saved);
    }

    @Override
    @Transactional
    public void delete(Integer userId, Integer noteId) {
        validateUserId(userId);
        if (noteId == null) {
            throw new BusinessException("noteId不能为空");
        }

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new BusinessException("日程不存在"));
        if (!userId.equals(note.getUserId())) {
            throw new BusinessException("无权限删除该日程");
        }

        try {
            noteRepository.delete(note);
            log.info("note_delete success userId={} noteId={}", userId, noteId);
        } catch (Exception e) {
            log.error("note_delete fail userId={} noteId={}", userId, noteId, e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void updateStatus(Integer userId, Integer noteId, Integer status) {
        validateUserId(userId);
        if (noteId == null) {
            throw new BusinessException("noteId不能为空");
        }
        if (status == null || (status != 0 && status != 1)) {
            throw new BusinessException("status只能是0/1");
        }

        // 先按 noteId 查询，不存在返回“日程不存在”
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new BusinessException("日程不存在"));

        // 再校验归属，无权限给出明确提示
        if (!userId.equals(note.getUserId())) {
            throw new BusinessException("无权限修改该日程");
        }

        note.setStatus(status);
        try {
            noteRepository.save(note);
            log.info("note_update_status success userId={} noteId={} status={}", userId, noteId, status);
        } catch (Exception e) {
            log.error("note_update_status fail userId={} noteId={} status={}", userId, noteId, status, e);
            throw e;
        }
    }

    @Override
    public List<NoteListItem> list(Integer userId) {
        validateUserId(userId);
        List<Note> notes = noteRepository.findAllByUserIdOrderByPlanTimeAsc(userId);

        List<NoteListItem> result = new ArrayList<>(notes.size());
        for (Note n : notes) {
            result.add(toListItem(n));
        }
        return result;
    }

    private void validateUserId(Integer userId) {
        if (userId == null) {
            // 正常情况下会被拦截器挡住，这里做兜底
            throw new BusinessException(400, "未登录");
        }
    }

    /**
     * 添加/编辑共用校验逻辑。
     */
    private void validateAddOrUpdate(String title, Long planTime, Integer priority) {
        if (!StringUtils.hasText(title)) {
            throw new BusinessException("标题不能为空");
        }
        String trimmedTitle = title.trim();
        if (trimmedTitle.length() < 1 || trimmedTitle.length() > 50) {
            throw new BusinessException("标题长度需为1-50字");
        }

        if (priority == null || (priority != 1 && priority != 2 && priority != 3)) {
            throw new BusinessException("优先级只能是1/2/3");
        }

        if (planTime == null) {
            throw new BusinessException("计划时间不能为空");
        }
        long now = System.currentTimeMillis();
        if (planTime <= now) {
            throw new BusinessException("计划时间必须大于当前时间");
        }
    }

    private NoteListItem toListItem(Note n) {
        NoteListItem item = new NoteListItem();
        item.setNoteId(n.getId());
        item.setTitle(n.getTitle());
        item.setContent(n.getContent());
        item.setPlanTime(n.getPlanTime());
        item.setPriority(n.getPriority());
        item.setRemindTime(n.getRemindTime());
        item.setStatus(n.getStatus());
        return item;
    }
}

