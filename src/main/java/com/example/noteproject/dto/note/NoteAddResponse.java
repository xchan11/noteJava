package com.example.noteproject.dto.note;

import lombok.Data;

/**
 * 添加/编辑日程成功响应数据。
 */
@Data
public class NoteAddResponse {
    private Integer noteId;
    private String title;
}

