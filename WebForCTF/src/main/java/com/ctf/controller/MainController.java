package com.ctf.controller;

import com.ctf.model.User;
import com.ctf.service.UserService;
import com.ctf.service.ChallengeService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private ChallengeService challengeService;

    @Autowired
    private com.ctf.session.SessionRegistry sessionRegistry;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");
        String username = (String) session.getAttribute("username");
        User currentUser = (User) session.getAttribute("user");

        model.addAttribute("isAuthenticated", isAuthenticated != null && isAuthenticated);
        model.addAttribute("username", username);
        model.addAttribute("currentUser", currentUser);

        return "index";
    }

    @GetMapping("/top3")
    @ResponseBody
    public List<Map<String, Object>> getTop3() {
        String backendUrl = "http://backend:8080/top3";

        try {
            RestTemplate rest = new RestTemplate();
            ResponseEntity<List> response = rest.getForEntity(backendUrl, List.class);

            return response.getBody(); // JSON → JS
        } catch (Exception e) {
            return List.of(
                    Map.of("login", "ERROR", "points", 0)
            );
        }
    }

    @GetMapping("/auth")
    public String authPage(@RequestParam(value = "register", required = false) String register,
                           @RequestParam(value = "error", required = false) String error,
                           Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAuthenticated", isAuthenticated != null && isAuthenticated);
        model.addAttribute("isLogin", register == null);

        if (error != null) {
            model.addAttribute("error", "Неверное имя пользователя или пароль");
        }
        return "auth";
    }

    @PostMapping("/login")
    public String loginUser(
            @RequestParam String username,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        if (sessionRegistry.isUserActive(username)) {
            model.addAttribute("error", "Пользователь уже вошёл в систему в другой сессии");
            model.addAttribute("isLogin", true);
            return "auth";
        }
        try {

            logger.info("LOGIN ATTEMPT: username={} sessionID={}", username, session.getId());

            Map<String, String> payload = Map.of(
                    "login", username,
                    "password", password
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);
            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.postForEntity(
                    "http://backend:8080/api/auth/login", // backend URL
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                session.setAttribute("username", username);
                session.setAttribute("isAuthenticated", true);

                sessionRegistry.registerSession(session.getId(), username);
                logger.info("SESSION REGISTERED: {} -> {}", session.getId(), username);

                logger.info("LOGIN SUCCESS: username={} sessionID={}", username, session.getId());
                logger.info("SESSION ATTRIBUTES: username={}, isAuthenticated={}",
                        session.getAttribute("username"),
                        session.getAttribute("isAuthenticated"));

                return "redirect:/";
            } else {
                logger.warn("LOGIN FAILED: username={} sessionID={} status={}",
                        username, session.getId(), response.getStatusCode());
                model.addAttribute("error", "Неверный логин или пароль");
                model.addAttribute("isLogin", true);
                return "auth";
            }

        } catch (HttpClientErrorException.Unauthorized e) {
            logger.warn("LOGIN FAILED: username={} reason=401 Unauthorized sessionID={}",
                    username, session.getId());
            model.addAttribute("error", "Неверный логин или пароль");
            model.addAttribute("isLogin", true);
            return "auth";
        } catch (Exception e) {
            logger.error("LOGIN ERROR: username={} exception={} sessionID={}",
                    username, e.getMessage(), session.getId());
            model.addAttribute("error", "Ошибка при входе: " + e.getMessage());
            model.addAttribute("isLogin", true);
            return "auth";
        }
    }

    @GetMapping("/api/sessions")
    @ResponseBody
    public List<Map<String, String>> getActiveSessions() {
        return sessionRegistry.getActiveSessions().entrySet().stream()
                .map(e -> Map.of(
                        "sessionId", e.getKey(),
                        "username", e.getValue()
                ))
                .toList();
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            @RequestParam String email,
            HttpSession session,
            Model model) {

        try {

            if (username == null || username.trim().isEmpty()) {
                model.addAttribute("error", "Имя пользователя обязательно");
                return showRegisterForm(model, session);
            }

            if (email == null || email.trim().isEmpty()) {
                model.addAttribute("error", "Email обязателен");
                return showRegisterForm(model, session);
            }

            if (password == null || password.isEmpty()) {
                model.addAttribute("error", "Пароль обязателен");
                return showRegisterForm(model, session);
            }

            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Пароли не совпадают");
                return showRegisterForm(model, session);
            }

            if (password.length() < 6) {
                model.addAttribute("error", "Пароль должен содержать минимум 6 символов");
                return showRegisterForm(model, session);
            }

            User user = userService.registerUser(username.trim(), password, email.trim());

            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());

            return "redirect:/?registration=success";

        } catch (RuntimeException e) {

            model.addAttribute("error", e.getMessage());
            return showRegisterForm(model, session);
        }
    }

    private String showRegisterForm(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAuthenticated", isAuthenticated != null && isAuthenticated);
        model.addAttribute("isLogin", false);
        return "auth";
    }

    @GetMapping("/check-username")
    @ResponseBody
    public String checkUsername(@RequestParam String username) {
        if (username == null || username.trim().length() < 3) {
            return "invalid";
        }
        return userService.usernameExists(username.trim()) ? "exists" : "available";
    }

    @GetMapping("/check-email")
    @ResponseBody
    public String checkEmail(@RequestParam String email) {
        if (email == null || email.trim().isEmpty()) {
            return "invalid";
        }
        return userService.emailExists(email.trim().toLowerCase()) ? "exists" : "available";
    }

    @GetMapping("/users")
    public String showUsers(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAuthenticated", isAuthenticated != null && isAuthenticated);

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        sessionRegistry.removeSession(session.getId());
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/debug")
    public String debugPage(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAuthenticated", isAuthenticated != null && isAuthenticated);

        String backendUrl = "http://backend:8080/debug/public/ping";
        RestTemplate restTemplate = new RestTemplate();
        String backendResponse;
        try {
            // Исправленная строка - используем getForObject вместо getForEntity
            backendResponse = restTemplate.getForObject(backendUrl, String.class);
        } catch (Exception e) {
            backendResponse = "Ошибка при обращении к бэку: " + e.getMessage();
        }

        model.addAttribute("pingResponse", backendResponse);
        return "debug";
    }

    @GetMapping("/category/{category}")
    public String showCategory(@PathVariable String category, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAuthenticated", isAuthenticated != null && isAuthenticated);
        model.addAttribute("category", category);

        switch (category.toLowerCase()) {
            case "pwn":
                return "categories/pwn";
            case "web":
                return "categories/web";
            case "crypto":
                return "categories/crypto";
            default:
                return "redirect:/";
        }
    }


    @GetMapping("/challenges/web")
    public String webChallengesOverview(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("isAuthenticated", isAuthenticated != null && isAuthenticated);


        List<Map<String, Object>> challenges = List.of(
                Map.<String, Object>of("title", "SQL Injection Basic", "points", 100, "difficulty", "easy", "url", "/challenges/sqli"),
                Map.<String, Object>of("title", "Authentication Bypass", "points", 120, "difficulty", "easy", "url", "/challenges/auth-bypass"),
                Map.<String, Object>of("title", "XSS Challenge", "points", 200, "difficulty", "medium", "url", "/challenges/xss"),
                Map.<String, Object>of("title", "CSRF Challenge", "points", 150, "difficulty", "medium", "url", "/challenges/csrf"),
                Map.<String, Object>of("title", "Path Traversal", "points", 250, "difficulty", "hard", "url", "/challenges/path-traversal")
        );

        model.addAttribute("challenges", challenges);
        return "categories/web";
    }

    @PostMapping("/api/challenges/validate")
    @ResponseBody
    public String validateChallengeFlag(@RequestBody Map<String, String> request) {
        try {
            String challengeName = request.get("challengeName");
            String flag = request.get("flag");

            System.out.println("Validating flag for challenge: " + challengeName);
            System.out.println("Flag: " + flag);

            boolean isValid = challengeService.validateFlagByChallengeName(challengeName, flag);

            if (isValid) {
                System.out.println("Flag validation SUCCESS for: " + challengeName);
                return "{\"success\": true, \"message\": \"🎉 Флаг верный! Задание выполнено.\"}";
            } else {
                System.out.println("Flag validation FAILED for: " + challengeName);
                return "{\"success\": false, \"message\": \"❌ Неверный флаг. Попробуйте еще раз.\"}";
            }
        } catch (Exception e) {
            System.err.println("Error validating flag: " + e.getMessage());
            return "{\"success\": false, \"message\": \"❌ Ошибка сервера при проверке флага\"}";
        }
    }
}