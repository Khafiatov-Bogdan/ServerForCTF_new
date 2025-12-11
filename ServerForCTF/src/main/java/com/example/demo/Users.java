package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String login;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private int points = 0;
    @Column(name = "points_lab", nullable = false)
    private int pointsLab = 0;


    public Users() {}

    public Users(String login, String password, int points, int pointsLab) {
        this.login = login;
        this.password = password;
        this.points = points;
        this.pointsLab = pointsLab;
    }


    // ======================
    // DTO для отображения очков
    // ======================
    public static class UserNamePointsDTO {
        private String name;
        private int points;
        private int pointsLab;

        public UserNamePointsDTO(String name, int points, int pointsLab) {
            this.name = name;
            this.points = points;
            this.pointsLab = pointsLab;
        }

        public String getName() { return name; }
        public int getPoints() { return points; }
        public int getPointsLab() { return pointsLab; }
    }

    public int getPointsLab() {
        return pointsLab;
    }

    public void setPointsLab(int pointsLab) {
        this.pointsLab = pointsLab;
    }

    public Long getId() { return id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    @Override
    public String toString() {
        return "Users{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", points=" + points +
                ", pointsLab=" + pointsLab +
                '}';
    }
}
