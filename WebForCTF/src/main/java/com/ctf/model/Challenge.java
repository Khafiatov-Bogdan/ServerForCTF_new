package com.ctf.model;

import jakarta.persistence.*;

@Entity
@Table(name = "challenges")
public class Challenge {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false)
    private String category;
    
    private Integer points;
    private String difficulty;
    
    @Column(unique = true)
    private String flag;
    
    private String solution;
    private String hints;
    

    public Challenge() {}
    
    public Challenge(String title, String description, String category, Integer points, 
                   String difficulty, String flag, String solution, String hints) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.points = points;
        this.difficulty = difficulty;
        this.flag = flag;
        this.solution = solution;
        this.hints = hints;
    }
    

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }
    
    public String getDifficulty() { return difficulty; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    
    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }
    
    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }
    
    public String getHints() { return hints; }
    public void setHints(String hints) { this.hints = hints; }
}