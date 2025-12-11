package com.example.demo.repository;

import com.example.demo.Promo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PromoRepository extends JpaRepository<Promo, Long> {

    Optional<Promo> findByName(String name);
}
