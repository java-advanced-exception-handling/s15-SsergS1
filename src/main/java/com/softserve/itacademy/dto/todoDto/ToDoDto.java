package com.softserve.itacademy.dto.todoDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ToDoDto {

    private Long id;

    @NotBlank(message = "The 'title' cannot be empty")
    private String title;

    private LocalDateTime createdAt;

    @NotNull(message = "The 'ownerId' cannot be null")
    private Long ownerId;

    private String ownerName;
}
