package com.softserve.itacademy.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDto {
    private Long id;

    @NotBlank(message = "The 'name' cannot be empty")
    private String name;

    @NotBlank(message = "The 'priority' cannot be empty")
    private String priority;

    @NotNull(message = "The 'todoId' cannot be null")
    private Long todoId;

    private Long stateId;
}
