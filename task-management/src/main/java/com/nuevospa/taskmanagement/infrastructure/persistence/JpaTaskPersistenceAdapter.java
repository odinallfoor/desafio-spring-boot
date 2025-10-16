package com.nuevospa.taskmanagement.infrastructure.persistence;

import com.nuevospa.taskmanagement.application.output.TaskPersistancePort;
import com.nuevospa.taskmanagement.domain.model.Task;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class JpaTaskPersistenceAdapter  implements TaskPersistancePort {

    private final TaskRepository taskRepository;

    public JpaTaskPersistenceAdapter(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Override
    public Task save(Task task) {
        return taskRepository.save(task);
    }

    @Override
    public List<Task> findByUserId(Long userId) {
        return taskRepository.findByUserId(userId);
    }

    @Override
    public Optional<Task> findByIdAndUserId(Long id, Long userId) {
        return taskRepository.findByIdAndUserId(id, userId);
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public boolean existsByIdAndUserId(Long id, Long userId) {
        return taskRepository.existsByIdAndUserId(id, userId);
    }
}
