package com.softserve.itacademy.dto.todoDto;

import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import org.springframework.stereotype.Component;

@Component
public class ToDoDtoConverter {

    public ToDoDto toDto(ToDo todo) {
        return ToDoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .createdAt(todo.getCreatedAt())
                .ownerId(todo.getOwner().getId())
                .ownerName(todo.getOwner().getFirstName() + " " + todo.getOwner().getLastName())
                .build();
    }

    public ToDo toEntity(CreateToDoDto createDto, User owner) {
        ToDo todo = new ToDo();
        todo.setTitle(createDto.getTitle());
        todo.setOwner(owner);
        return todo;
    }

    public void fillFields(ToDo todo, UpdateToDoDto updateDto, User owner) {
        todo.setTitle(updateDto.getTitle());
        todo.setOwner(owner);
    }
}
