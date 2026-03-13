package com.example.noteproject.service;

import com.example.noteproject.dto.note.NoteAddRequest;
import com.example.noteproject.dto.note.NoteListItem;
import com.example.noteproject.dto.note.NoteUpdateRequest;

import java.util.List;

/**
 * 日程（Note）业务接口。
 */
public interface NoteService {

    NoteListItem add(Integer userId, NoteAddRequest request);

    NoteListItem update(Integer userId, NoteUpdateRequest request);

    void delete(Integer userId, Integer noteId);

    void updateStatus(Integer userId, Integer noteId, Integer status);

    List<NoteListItem> list(Integer userId);
}

