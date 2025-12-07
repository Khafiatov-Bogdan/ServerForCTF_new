package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "taskspwn")
public class TaskPWN {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private boolean solved = false;

    @Column(nullable = false)
    private int points;

    @Column(nullable = false)
    private String difficulty;

    public TaskPWN() {}

    public TaskPWN(String category, String description, String title, int points, String difficulty) {
        this.category = category;
        this.description = description;
        this.title = title;
        this.points = points;
        this.difficulty = difficulty;
    }

    public Long getId() { return id; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getTitle() { return title; }
    public String getDifficulty() { return difficulty; }

    public void setTitle(String title) { this.title = title; }

    public void setCategory(String category) { this.category = category; }

    public boolean isSolved() { return solved; }
    public void setSolved(boolean solved) { this.solved = solved; }

    public void setDescription(String description) { this.description = description; }

    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    @Override
    public String toString() {
        return "TaskPWN{" +
                "id=" + id +
                ", category=" + category +
                ", title='" + title + '\'' +
                ", category=" + category + '\'' +
                ", solved=" + solved +
                ", points=" + points +
                '}';
    }
}
