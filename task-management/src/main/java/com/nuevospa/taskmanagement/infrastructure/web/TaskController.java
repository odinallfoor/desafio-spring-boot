package com.nuevospa.taskmanagement.infrastructure.web;

import com.nuevospa.taskmanagement.application.dto.TaskMapper;
import com.nuevospa.taskmanagement.application.dto.TaskRequestDTO;
import com.nuevospa.taskmanagement.application.dto.TaskResponseDTO;
import com.nuevospa.taskmanagement.application.input.TaskManagementInputPort;
import com.nuevospa.taskmanagement.domain.model.Task;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskManagementInputPort taskManagementInputPort;
    private final TaskMapper taskMapper;

    public TaskController(TaskManagementInputPort taskManagementInputPort, TaskMapper taskMapper) {
        this.taskManagementInputPort = taskManagementInputPort;
        this.taskMapper = taskMapper;
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO taskRequestDTO){

        Task taskToCreate = taskMapper.toDomain(taskRequestDTO);
        Task createdTask = taskManagementInputPort.createTask(taskToCreate);
        TaskResponseDTO responseDTO = taskMapper.toResponseDTO(createdTask);

        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id){
        return taskManagementInputPort.getTaskById(id)
                .map(taskMapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public List<TaskResponseDTO> getAllTasks(){
        return taskManagementInputPort.getAllTask()
                .stream()
                .map(taskMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequestDTO taskRequestDTO){
        Task taskToUpdate = taskMapper.toDomain(taskRequestDTO);
        Task updatedTask = taskManagementInputPort.updateTask(id, taskToUpdate);

        return ResponseEntity.ok(taskMapper.toResponseDTO(updatedTask));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<TaskResponseDTO> completeTask(@PathVariable Long id){
        Task completedTask = taskManagementInputPort.completeTask(id);

        return ResponseEntity.ok(taskMapper.toResponseDTO(completedTask));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> deleteTask(@PathVariable Long id){
        taskManagementInputPort.deleteTask(id);

        return ResponseEntity.noContent().build();
    }
}
