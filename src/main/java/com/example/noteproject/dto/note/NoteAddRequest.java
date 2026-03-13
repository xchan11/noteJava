package com.example.noteproject.dto.note;

import lombok.Data;

/**
 * 添加日程请求参数。
 * 时间字段使用毫秒级时间戳（Long），不做时区转换。
 */
@Data
public class NoteAddRequest {
    private String title;
    private String content;
    private Long planTime;
    private Integer priority;
    private Long remindTime;
}

