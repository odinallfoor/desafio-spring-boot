package com.nuevospa.taskmanagement.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class TaskRequestDTO {

    @NotBlank(message = "El titulo es obligatorio.")
    @Size(min = 3, max = 100, message = "El titulo debe tener entre 3 y 100 caracteres.")
    private String title;

    @NotBlank(message = "La descripcion es obligatoria.")
    @Size(max = 500, message = "La descripcion no puede superar los 500 caracteres")
    private String description;
}
