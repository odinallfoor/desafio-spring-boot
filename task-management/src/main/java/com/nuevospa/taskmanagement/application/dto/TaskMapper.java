package com.nuevospa.taskmanagement.application.dto;

import com.nuevospa.taskmanagement.domain.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskMapper {

    public Task toDomain(TaskRequestDTO dto){
        return Task.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .build();
    }

    public TaskResponseDTO toResponseDTO(Task domain){
        TaskResponseDTO dto =new TaskResponseDTO();
        dto.setId(domain.getId());
        dto.setTitle(domain.getTitle());
        dto.setDescription(domain.getDescription());
        dto.setCompleted(domain.isCompleted());
        dto.setCreationDate(domain.getCreationDate());
        dto.setUserId(domain.getUserId());
        return dto;
    }
}
