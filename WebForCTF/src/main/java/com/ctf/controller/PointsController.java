package com.ctf.controller;

import com.ctf.SessionRegistry;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequestMapping("/points")
public class PointsController {

    @Autowired
    private SessionRegistry sessionRegistry;

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<String> addPoints(
            @RequestParam int amount,
            HttpSession session
    ) {
        String username = (String) session.getAttribute("username");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        if (username == null || isAuthenticated == null || !isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        if (!sessionRegistry.isUserActive(username)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Session not registered. Please login again.");
        }

        try {
            String backendUrl = "http://backend:8080/users/" + username + "/points/add/" + amount;
            RestTemplate rest = new RestTemplate();
            rest.postForEntity(backendUrl, null, String.class);

            return ResponseEntity.ok("Points added: " + amount);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Backend error: " + e.getMessage());
        }
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updatePoints(
            @RequestParam String username,
            @RequestParam int amount,
            HttpSession session
    ) {
        String currentUser = (String) session.getAttribute("username");
        Boolean isAuthenticated = (Boolean) session.getAttribute("isAuthenticated");

        if (currentUser == null || isAuthenticated == null || !isAuthenticated) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Не авторизован");
        }

        if (!"admin".equalsIgnoreCase(currentUser)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Только админ может изменять очки");
        }

        try {
            String backendUrl = "http://backend:8080/users/" + username + "/points/add/" + amount;
            RestTemplate rest = new RestTemplate();
            rest.postForEntity(backendUrl, null, String.class);

            return ResponseEntity.ok(Map.of("username", username, "pointsAdded", amount));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
