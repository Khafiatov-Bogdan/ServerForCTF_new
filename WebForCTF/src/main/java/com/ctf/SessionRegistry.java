package com.ctf;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionRegistry {

    // sessionId -> SessionInfo
    private final Map<String, SessionInfo> sessions = new ConcurrentHashMap<>();

    // username -> sessionId
    private final Map<String, String> userSessions = new ConcurrentHashMap<>();


    /**
     * Регистрирует новую сессию
     */
    public synchronized void registerSession(String sessionId, String username) {
        sessions.put(sessionId, new SessionInfo(username));
        userSessions.put(username, sessionId);
    }

    /**
     * Проверка активности пользователя по логину
     */
    public synchronized boolean isUserActive(String username) {
        return userSessions.containsKey(username);
    }

    /**
     * Удаляет сессию
     */
    public synchronized void removeSession(String sessionId) {
        SessionInfo info = sessions.remove(sessionId);
        if (info != null) {
            userSessions.remove(info.username);
        }
    }

    /**
     * ===============================
     * Методы для работы с тестами
     * ===============================
     */
    public synchronized boolean hasCompletedTest(String username) {
        String sessionId = userSessions.get(username);
        if (sessionId == null) return false;

        SessionInfo info = sessions.get(sessionId);
        return info != null && info.testCompleted;
    }

    public synchronized void markTestCompleted(String username) {
        String sessionId = userSessions.get(username);
        if (sessionId == null) return;

        SessionInfo info = sessions.get(sessionId);
        if (info != null) info.testCompleted = true;
    }


    /**
     * Получение списка активных сессий
     */
    public Map<String, String> getActiveSessions() {
        return Map.copyOf(userSessions);
    }

    /**
     * Внутренний класс информации о сессии
     */
    private static class SessionInfo {
        String username;
        boolean testCompleted = false; // флаг прохождения теста

        SessionInfo(String username) {
            this.username = username;
        }
    }
}
