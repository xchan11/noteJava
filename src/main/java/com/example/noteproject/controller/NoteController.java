package com.example.noteproject.controller;

import com.example.noteproject.common.ApiResponse;
import com.example.noteproject.dto.note.NoteAddRequest;
import com.example.noteproject.dto.note.NoteListItem;
import com.example.noteproject.dto.note.NoteUpdateRequest;
import com.example.noteproject.dto.note.NoteUpdateStatusRequest;
import com.example.noteproject.service.NoteService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 日程（Note）接口。
 * 所有 /note/** 接口均通过 Session/Cookie 鉴权，从 Session 获取 userId，无需前端传 userId。
 */
@RestController
@RequestMapping("/note")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    /**
     * 添加日程。
     * POST /note/add
     */
    @PostMapping("/add")
    public ApiResponse<NoteListItem> add(@RequestBody NoteAddRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        NoteListItem data = noteService.add(userId, request);
        return ApiResponse.success(200, "日程添加成功", data);
    }

    /**
     * 编辑日程。
     * PUT /note/update
     */
    @PutMapping("/update")
    public ApiResponse<NoteListItem> update(@RequestBody NoteUpdateRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        NoteListItem data = noteService.update(userId, request);
        return ApiResponse.success(200, "日程编辑成功", data);
    }

    /**
     * 删除日程（物理删除）。
     * DELETE /note/delete?noteId=1
     *
     * 说明：Retrofit 的 DELETE 通常不允许携带 body，因此使用 Query 参数接收 noteId。
     */
    @DeleteMapping("/delete")
    public ApiResponse<Void> delete(@RequestParam("noteId") Integer noteId, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        noteService.delete(userId, noteId);
        return ApiResponse.success(200, "删除成功", null);
    }

    /**
     * 更新日程状态（0/1）。
     * PUT /note/updateStatus
     */
    @PutMapping("/updateStatus")
    public ApiResponse<Void> updateStatus(@RequestBody NoteUpdateStatusRequest request, HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        Integer noteId = request == null ? null : request.getNoteId();
        Integer status = request == null ? null : request.getStatus();
        noteService.updateStatus(userId, noteId, status);
        return ApiResponse.success(200, "状态更新成功", null);
    }

    /**
     * 查询当前用户所有日程，按 planTime 升序排序。
     * GET /note/list
     */
    @GetMapping("/list")
    public ApiResponse<List<NoteListItem>> list(HttpServletRequest httpRequest) {
        Integer userId = (Integer) httpRequest.getSession().getAttribute("userId");
        List<NoteListItem> data = noteService.list(userId);
        return ApiResponse.success(200, "查询成功", data);
    }
}

