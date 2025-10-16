package com.nuevospa.taskmanagement.application.output;

import com.nuevospa.taskmanagement.domain.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskPersistancePort {

    Task save(Task task);
    List<Task> findByUserId(Long userId);
    Optional<Task> findByIdAndUserId(Long id, Long userId);
    void deleteTask(Long id);

    boolean existsByIdAndUserId(Long id, Long userId);
}
