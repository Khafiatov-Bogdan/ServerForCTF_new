package com.ctf.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();

        // Разрешаем доступ без авторизации
        if (
                uri.equals("/") ||
                        uri.startsWith("/auth") ||
                        uri.startsWith("/login") ||
                        uri.startsWith("/check-username") ||
                        uri.startsWith("/check-email") ||
                        uri.startsWith("/top3") ||
                        uri.startsWith("/css") ||
                        uri.startsWith("/js") ||
                        uri.startsWith("/images") ||
                        uri.startsWith("/debug") ||
                        uri.startsWith("/api/challenges/validate")
        ) {
            return true;
        }

        HttpSession session = request.getSession(false);

        // Проверяем авторизацию
        if (session == null || session.getAttribute("isAuthenticated") == null) {
            response.sendRedirect("/auth");
            return false;
        }

        Boolean isAuth = (Boolean) session.getAttribute("isAuthenticated");

        if (!isAuth) {
            response.sendRedirect("/auth");
            return false;
        }

        return true;
    }
}
