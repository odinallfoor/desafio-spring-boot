package com.nuevospa.taskmanagement.domain.service;

import com.nuevospa.taskmanagement.application.input.TaskManagementInputPort;
import com.nuevospa.taskmanagement.application.output.TaskPersistancePort;
import com.nuevospa.taskmanagement.domain.model.Task;
import com.nuevospa.taskmanagement.domain.model.user.User;
import com.nuevospa.taskmanagement.infrastructure.persistence.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService implements TaskManagementInputPort {

    private final TaskPersistancePort taskPersistancePort;
    private final UserRepository userRepository;

    public TaskService(TaskPersistancePort taskPersistancePort, UserRepository userRepository) {
        this.taskPersistancePort = taskPersistancePort;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || auth.getName() == null){
            throw new RuntimeException("No hay usuario autenticado en el contexto.");
        }

        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado: " + username));

        return user.getId();
    }

    @Override
    @Transactional
    public Task createTask(Task task) {
        task.setCreationDate(LocalDateTime.now());
        task.setCompleted(false);
        task.setUserId(getCurrentUserId());
        return taskPersistancePort.save(task);
    }

    @Override
    public Optional<Task> getTaskById(Long id) {
        Long userId = getCurrentUserId();
        return taskPersistancePort.findByIdAndUserId(id, userId);
    }

    @Override
    public List<Task> getAllTask() {
        Long userId = getCurrentUserId();
        return taskPersistancePort.findByUserId(userId);
    }

    @Override
    @Transactional
    public Task updateTask(Long id, Task taskDetails) {
        Long userId = getCurrentUserId();
        Task existingTask = taskPersistancePort.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Task no encontrada con el id : " + id));
        existingTask.setTitle(taskDetails.getTitle());
        existingTask.setDescription(taskDetails.getDescription());

        return taskPersistancePort.save(existingTask);
    }

    @Override
    public void deleteTask(Long id) {
        Long userId = getCurrentUserId();
        if (!taskPersistancePort.existsByIdAndUserId(id, userId)) {
            throw new RuntimeException("Task no encontrada o no Pertenece al usuario actual: " + id);
        }
        taskPersistancePort.deleteTask(id);
    }

    @Override
    public Task completeTask(Long id) {
        Long userId = getCurrentUserId();

        Task task = taskPersistancePort.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new RuntimeException("Task no encontrada con el id: " + id));
        task.setCompleted(true);
        return taskPersistancePort.save(task);
    }
}
