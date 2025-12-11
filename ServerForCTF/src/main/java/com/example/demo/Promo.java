package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "promo")
public class Promo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private boolean isUsed = false;

    @Column(nullable = false)
    private int points; // количество баллов, которое даёт промокод

    public Promo() {}

    public Promo(String name, int points) {
        this.name = name;
        this.points = points;
        this.isUsed = false;
    }

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean getIsUsed() { return isUsed; }
    public void setIsUsed(boolean isUsed) { this.isUsed = isUsed; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    @Override
    public String toString() {
        return "Promo{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isUsed=" + isUsed +
                ", points=" + points +
                '}';
    }
}
