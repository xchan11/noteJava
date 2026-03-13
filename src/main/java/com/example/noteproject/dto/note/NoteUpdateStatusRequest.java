package com.example.noteproject.dto.note;

import lombok.Data;

/**
 * 更新日程状态请求参数。
 */
@Data
public class NoteUpdateStatusRequest {
    private Integer noteId;
    /**
     * 0 未完成 / 1 已完成
     */
    private Integer status;
}

