package com.ctf.controller;

import com.ctf.model.User;
import com.ctf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class LeaderboardController {

    @Autowired
    private UserService userService;

    @GetMapping("/leaderboard")
    public List<LeaderboardEntry> getLeaderboard() {
        List<User> topUsers = userService.getTopUsers();


        if (topUsers == null || topUsers.isEmpty()) {
            return List.of();
        }

        return topUsers.stream()
                .map(user -> new LeaderboardEntry(
                        user.getUsername(),
                        user.getScore() != null ? user.getScore() : 0,
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/leaderboard/top10")
    public List<LeaderboardEntry> getTop10() {
        List<User> allUsers = userService.getTopUsers();

        if (allUsers == null || allUsers.isEmpty()) {
            return List.of();
        }

        return allUsers.stream()
                .limit(10)
                .map(user -> new LeaderboardEntry(
                        user.getUsername(),
                        user.getScore() != null ? user.getScore() : 0,
                        user.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }


    public static class LeaderboardEntry {
        private String username;
        private Integer score;
        private java.time.LocalDateTime createdAt;

        public LeaderboardEntry(String username, Integer score, java.time.LocalDateTime createdAt) {
            this.username = username;
            this.score = score;
            this.createdAt = createdAt;
        }


        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }

        public Integer getScore() { return score; }
        public void setScore(Integer score) { this.score = score; }

        public java.time.LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(java.time.LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}