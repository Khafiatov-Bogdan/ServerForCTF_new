package com.example.demo.repository;

import com.example.demo.TaskPWN;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


import java.util.List;

@Repository
public interface TaskPWNRepository extends JpaRepository<TaskPWN, Long> {
    List<TaskPWN> findBySolvedFalse();
    Optional<TaskPWN> findByTitle(String title);
}
