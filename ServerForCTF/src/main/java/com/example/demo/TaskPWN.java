package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "taskspwn")
public class TaskPWN {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "category", nullable = false)
    private String category;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "solved", nullable = false)
    private boolean solved = false;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "difficulty", nullable = false)
    private String difficulty;

    @Column(name = "flag", nullable = false)
    private String flag;

    @Column(name = "binary_name", nullable = false)
    private String binary;

    public TaskPWN() {}

    public TaskPWN(String category, String description, String title, int points, String difficulty, String flag, String binary) {
        this.category = category;
        this.description = description;
        this.title = title;
        this.points = points;
        this.difficulty = difficulty;
        this.flag = flag;
        this.binary = binary;
    }

    public Long getId() { return id; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public String getTitle() { return title; }
    public String getDifficulty() { return difficulty; }
    public String getFlag() { return flag; }
    public String getBinary() { return binary; }

    public void setTitle(String title) { this.title = title; }

    public void setCategory(String category) { this.category = category; }

    public boolean isSolved() { return solved; }
    public void setSolved(boolean solved) { this.solved = solved; }

    public void setDescription(String description) { this.description = description; }

    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public void setFlag(String flag) { this.flag = flag; }

    public void setBinary(String binary) { this.binary = binary; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    @Override
    public String toString() {
        return "TaskPWN{" +
                "id=" + id + '\'' +
                ", title=" + title + '\'' +
                ", category=" + category + '\'' +
                ", solved=" + solved + '\'' +
                ", points=" + points + '\'' +
                ", binary=" + binary + '\'' +
                '}';
    }
}
