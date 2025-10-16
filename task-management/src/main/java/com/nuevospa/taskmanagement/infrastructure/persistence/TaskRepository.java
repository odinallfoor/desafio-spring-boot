package com.nuevospa.taskmanagement.infrastructure.persistence;

import com.nuevospa.taskmanagement.domain.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    /**
     * Busca todas las tareas asociadas a un ID de usuario específico.
     */
    List<Task> findByUserId(Long userId);

    /**
     * Encuentra una tarea específica por su ID y el ID del usuario propietario.
     */
    Optional<Task> findByIdAndUserId(Long id, Long userId);

    /**
     * Verifica si una tarea existe para un usuario específico.
     */
    boolean existsByIdAndUserId(Long id, Long userId);
}
