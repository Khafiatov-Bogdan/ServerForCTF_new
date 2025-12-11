package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private double percent;
    private int grade;

    public Report() {}

    public Report(String name, double percent, int grade) {
        this.name = name;
        this.percent = percent;
        this.grade = grade;
    }

    // геттеры/сеттеры
    public Long getId() { return id; }
    public String getName() { return name; }
    public double getPercent() { return percent; }
    public int getGrade() { return grade; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPercent(double percent) { this.percent = percent; }
    public void setGrade(int grade) { this.grade = grade; }
}
