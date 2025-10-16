package com.nuevospa.taskmanagement.infrastructure.persistence;

import com.nuevospa.taskmanagement.domain.model.status.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    Optional<TaskStatus> findByNombre(String nombre);
}
