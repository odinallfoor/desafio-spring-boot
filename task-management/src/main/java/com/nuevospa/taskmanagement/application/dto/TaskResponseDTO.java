package com.nuevospa.taskmanagement.application.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskResponseDTO {

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime creationDate;
    private Long userId;
}
