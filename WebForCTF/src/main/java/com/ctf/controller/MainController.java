package com.ctf.controller;

import com.ctf.model.User;
import com.ctf.service.UserService;
import jakarta.servlet.http.HttpSession;
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
import org.slf4j.Logger;
import java.util.List;
import java.util.Map;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private com.ctf.SessionRegistry sessionRegistry;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);


    // ==============================
    // HOME PAGE
    // ==============================
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");
        String username = (String) session.getAttribute("username");

        model.addAttribute("isAuthenticated", isAuthenticated != null && isAuthenticated);
        model.addAttribute("username", username);

        return "index";
    }


    // ==============================
    // TOP-3 API
    // ==============================
    @GetMapping("/top3")
    @ResponseBody
    public List<Map<String, Object>> getTop3() {
        String backendUrl = "http://backend:8080/top3";

        try {
            RestTemplate rest = new RestTemplate();
            ResponseEntity<List> response = rest.getForEntity(backendUrl, List.class);
            return response.getBody();
        } catch (Exception e) {
            return List.of(
                    Map.of("login", "ERROR", "points", 0)
            );
        }
    }


    // ==============================
    // AUTH PAGE
    // ==============================
    @GetMapping("/auth")
    public String authPage(@RequestParam(value = "register", required = false) String register,
                           @RequestParam(value = "error", required = false) String error,
                           Model model) {
        model.addAttribute("isLogin", register == null);
        if (error != null) {
            model.addAttribute("error", "Неверное имя пользователя или пароль");
        }
        return "auth";
    }


    // ==============================
    // LOGIN
    // ==============================
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
                    "http://backend:8080/api/auth/login",
                    entity,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {

                session.setAttribute("username", username);
                session.setAttribute("isAuthenticated", true);

                sessionRegistry.registerSession(session.getId(), username);

                logger.info("LOGIN SUCCESS: username={} sessionID={}", username, session.getId());

                // 🔥 ДОБАВЛЕНО ИЗ ПЕРВОГО ФАЙЛА
                if ("admin".equalsIgnoreCase(username)) {
                    return "redirect:/admin-users.html";
                }

                return "redirect:/";
            } else {
                model.addAttribute("error", "Неверный логин или пароль");
                model.addAttribute("isLogin", true);
                return "auth";
            }

        } catch (HttpClientErrorException.Unauthorized e) {
            model.addAttribute("error", "Неверный логин или пароль");
            model.addAttribute("isLogin", true);
            return "auth";

        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при входе: " + e.getMessage());
            model.addAttribute("isLogin", true);
            return "auth";
        }
    }


    // ==============================
    // ADMIN PAGE (ДОБАВЛЕНО)
    // ==============================
    @GetMapping("/admin-users.html")
    public String adminUsers(HttpSession session, Model model) {
        String username = (String) session.getAttribute("username");

        if (username == null || !"admin".equalsIgnoreCase(username)) {
            return "redirect:/auth";
        }

        model.addAttribute("currentUser", username);
        return "admin-users";
    }


    // ==============================
    // ACTIVE SESSIONS API
    // ==============================
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


    // ==============================
    // REGISTER
    // ==============================
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
                return showRegisterForm(model);
            }

            if (email == null || email.trim().isEmpty()) {
                model.addAttribute("error", "Email обязателен");
                return showRegisterForm(model);
            }

            if (password == null || password.isEmpty()) {
                model.addAttribute("error", "Пароль обязателен");
                return showRegisterForm(model);
            }

            if (!password.equals(confirmPassword)) {
                model.addAttribute("error", "Пароли не совпадают");
                return showRegisterForm(model);
            }

            if (password.length() < 6) {
                model.addAttribute("error", "Пароль должен содержать минимум 6 символов");
                return showRegisterForm(model);
            }

            User user = userService.registerUser(username.trim(), password, email.trim());

            session.setAttribute("user", user);
            session.setAttribute("username", user.getUsername());

            return "redirect:/?registration=success";

        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return showRegisterForm(model);
        }
    }

    private String showRegisterForm(Model model) {
        model.addAttribute("isLogin", false);
        return "auth";
    }


    // ==============================
    // CHECK USERNAME/EMAIL
    // ==============================
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


    // ==============================
    // USERS LIST
    // ==============================
    @GetMapping("/users")
    public String showUsers(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        model.addAttribute("currentUser", currentUser);

        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "users";
    }


    // ==============================
    // LOGOUT
    // ==============================
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        sessionRegistry.removeSession(session.getId());
        session.invalidate();
        return "redirect:/";
    }


    // ==============================
    // DEBUG PAGE
    // ==============================
    @GetMapping("/debug")
    public String debugPage(Model model) {
        String backendUrl = "http://backend:8080/debug/public/ping";
        RestTemplate restTemplate = new RestTemplate();
        String backendResponse;

        try {
            backendResponse = restTemplate.getForObject(backendUrl, String.class);
        } catch (Exception e) {
            backendResponse = "Ошибка при обращении к бэку: " + e.getMessage();
        }

        model.addAttribute("pingResponse", backendResponse);
        return "debug";
    }


    // ==============================
    // CATEGORY ROUTES
    // ==============================
    @GetMapping("/category/{category}")
    public String showCategory(@PathVariable String category, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("user");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("category", category);

        switch (category.toLowerCase()) {
            case "pwn":
                return "pwn";
            case "web":
                return "web";
            case "crypto":
                return "crypto";
            default:
                return "redirect:/";
        }
    }
}
