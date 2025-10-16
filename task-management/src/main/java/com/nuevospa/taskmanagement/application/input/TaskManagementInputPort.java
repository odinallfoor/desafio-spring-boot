package com.nuevospa.taskmanagement.application.input;

import com.nuevospa.taskmanagement.domain.model.Task;

import java.util.List;
import java.util.Optional;

public interface TaskManagementInputPort {

    Task createTask(Task task);
    Optional<Task> getTaskById(Long id);
    List<Task> getAllTask();
    Task updateTask(Long id, Task taskDetails);
    void deleteTask(Long id);
    Task completeTask(Long id);
}
