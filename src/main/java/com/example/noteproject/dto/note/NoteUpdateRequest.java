package com.example.noteproject.dto.note;

import lombok.Data;

/**
 * 编辑日程请求参数。
 */
@Data
public class NoteUpdateRequest {
    private Integer noteId;
    private String title;
    private String content;
    private Long planTime;
    private Integer priority;
    private Long remindTime;
}

