package com.softserve.itacademy.dto.userDto;

import com.softserve.itacademy.model.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UpdateUserDto {

    private Long id;

    @NotBlank(message = "The 'firstName' cannot be empty")
    @Pattern(regexp = "[A-Z][a-z]+",
            message = "Must start with a capital letter followed by one or more lowercase letters")
    private String firstName;

    @NotBlank(message = "The 'lastName' cannot be empty")
    @Pattern(regexp = "[A-Z][a-z]+",
            message = "Must start with a capital letter followed by one or more lowercase letters")
    private String lastName;

    @NotBlank(message = "The 'email' cannot be empty")
    @Pattern(regexp = "[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    private String email;

    private UserRole role;

}
