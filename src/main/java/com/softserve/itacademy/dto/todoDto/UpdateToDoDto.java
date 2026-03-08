package com.softserve.itacademy.dto.todoDto;

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
public class UpdateToDoDto {

    @NotNull(message = "The 'id' cannot be null")
    private Long id;

    @NotBlank(message = "The 'title' cannot be empty")
    private String title;

    @NotNull(message = "The 'ownerId' cannot be null")
    private Long ownerId;
}
