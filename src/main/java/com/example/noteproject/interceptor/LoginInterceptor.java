package com.example.noteproject.interceptor;

import com.example.noteproject.common.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;

/**
 * 登录鉴权拦截器：通过 Cookie 中的 JSESSIONID 解析 Session，检查是否已登录。
 */
public class LoginInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        Object userId = (session == null) ? null : session.getAttribute("userId");

        if (userId == null) {
            String uri = request.getRequestURI();
            boolean isNoteApi = (uri != null && uri.startsWith("/note/"));

            // 前端兜底逻辑：/note/** 未登录也按 400 返回（非登录态视同权限不足），并给出明确 msg
            int code = isNoteApi ? 400 : 401;
            String message = isNoteApi ? "未登录" : "未登录或会话过期，请重新登录";

            ApiResponse<Void> body = ApiResponse.error(code, message);
            response.setStatus(isNoteApi ? HttpServletResponse.SC_BAD_REQUEST : HttpServletResponse.SC_UNAUTHORIZED);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(OBJECT_MAPPER.writeValueAsString(body));
            return false;
        }
        return true;
    }
}

