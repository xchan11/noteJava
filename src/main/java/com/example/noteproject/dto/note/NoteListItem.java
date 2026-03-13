package com.example.noteproject.dto.note;

import lombok.Data;

/**
 * 日程列表返回项。
 */
@Data
public class NoteListItem {
    private Integer noteId;
    private String title;
    private String content;
    private Long planTime;
    private Integer priority;
    private Long remindTime;
    /**
     * 日程状态：0 未完成 / 1 已完成
     */
    private Integer status;
}

