package com.ctf.repository;

import com.ctf.model.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long> {
    
    List<Challenge> findByCategory(String category);
    List<Challenge> findByDifficulty(String difficulty);
    Optional<Challenge> findByTitle(String title);
    Optional<Challenge> findByFlag(String flag);
    

    @Query(value = "SELECT * FROM challenges WHERE title = :title", nativeQuery = true)
    List<Challenge> findVulnerableByTitle(@Param("title") String title);
    

    @Query("SELECT c FROM Challenge c WHERE c.title = :title")
    List<Challenge> findSafeByTitle(@Param("title") String title);
}