package com.example.demo.repository;

import com.example.demo.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findBySolvedFalse();
    Optional<Task> findByTitle(String title);
}
