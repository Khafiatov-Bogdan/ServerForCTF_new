package com.example.demo.service;

import com.example.demo.Task;
import com.example.demo.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAvailableTasks() {
        return taskRepository.findBySolvedFalse();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public Task markTaskSolved(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Задание не найдено"));

        if (!task.isSolved()) {
            task.setSolved(true);
            taskRepository.save(task);
        }

        return task;
    }

    public Optional<Task> getTaskByTitle(String title) {
        return taskRepository.findByTitle(title);
    }

    public Task markTaskSolvedByTitle(String title) {
        Task task = taskRepository.findByTitle(title)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        if (!task.isSolved()) {
            task.setSolved(true);
            taskRepository.save(task);
        }
        return task;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }
}
