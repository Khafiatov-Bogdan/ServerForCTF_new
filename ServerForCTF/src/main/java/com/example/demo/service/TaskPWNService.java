package com.example.demo.service;

import com.example.demo.TaskPWN;
import com.example.demo.repository.TaskPWNRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskPWNService {

    private final TaskPWNRepository taskPWNRepository;

    public TaskPWNService(TaskPWNRepository taskPWNRepository) {
        this.taskPWNRepository = taskPWNRepository;
    }

    public List<TaskPWN> getAvailableTasks() {
        return taskPWNRepository.findBySolvedFalse();
    }

    public Optional<TaskPWN> getTaskById(Long id) {
        return taskPWNRepository.findById(id);
    }

    public TaskPWN markTaskSolved(Long id) {
        TaskPWN task = taskPWNRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задание не найдено"));

        if (!task.isSolved()) {
            task.setSolved(true);
            taskPWNRepository.save(task);
        }

        return task;
    }

    public Optional<TaskPWN> getTaskByTitle(String title) {
        return taskPWNRepository.findByTitle(title);
    }

    public TaskPWN markTaskSolvedByTitle(String title) {
        TaskPWN task = taskPWNRepository.findByTitle(title)
                .orElseThrow(() -> new RuntimeException("TaskPWN not found"));
        if (!task.isSolved()) {
            task.setSolved(true);
            taskPWNRepository.save(task);
        }
        return task;
    }

    public TaskPWN createTask(TaskPWN task) {
        return taskPWNRepository.save(task);
    }
}
