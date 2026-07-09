package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.todoDto.CreateToDoDto;
import com.softserve.itacademy.dto.todoDto.ToDoDtoConverter;
import com.softserve.itacademy.dto.todoDto.UpdateToDoDto;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.service.ToDoService;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/todos")
@RequiredArgsConstructor
@Slf4j
public class ToDoController {

    private final ToDoService todoService;
    private final TaskService taskService;
    private final UserService userService;
    private final ToDoDtoConverter todoDtoConverter;

    @GetMapping("/create/users/{owner_id}")
    public String createToDoForm(@PathVariable("owner_id") Long ownerId, Model model) {
        log.info("GET request for create ToDo form for owner id: {}", ownerId);
        userService.readById(ownerId);
        CreateToDoDto todoDto = new CreateToDoDto();
        todoDto.setOwnerId(ownerId);
        model.addAttribute("todo", todoDto);
        model.addAttribute("ownerId", ownerId);
        return "create-todo";
    }

    @PostMapping("/create/users/{owner_id}")
    public String createToDo(@PathVariable("owner_id") Long ownerId,
                             @Validated @ModelAttribute("todo") CreateToDoDto todoDto,
                             BindingResult result,
                             Model model) {
        log.info("POST request to create ToDo for owner id: {}", ownerId);
        if (result.hasErrors()) {
            log.warn("Validation failed for ToDo creation: {}", result.getAllErrors());
            model.addAttribute("ownerId", ownerId);
            return "create-todo";
        }
        User owner = userService.readById(ownerId);
        ToDo todo = todoDtoConverter.toEntity(todoDto, owner);
        todoService.create(todo);
        log.info("ToDo created successfully for owner id: {}", ownerId);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/{todo_id}/update/users/{owner_id}")
    public String updateToDoForm(@PathVariable("todo_id") Long todoId,
                                 @PathVariable("owner_id") Long ownerId,
                                 Model model) {
        log.info("GET request for update ToDo form id: {}", todoId);
        ToDo todo = todoService.readById(todoId);
        UpdateToDoDto todoDto = UpdateToDoDto.builder()
                .id(todo.getId())
                .title(todo.getTitle())
                .ownerId(todo.getOwner().getId())
                .build();
        model.addAttribute("todo", todoDto);
        return "update-todo";
    }

    @PostMapping("/{todo_id}/update/users/{owner_id}")
    public String updateToDo(@PathVariable("todo_id") Long todoId,
                             @PathVariable("owner_id") Long ownerId,
                             @Validated @ModelAttribute("todo") UpdateToDoDto todoDto,
                             BindingResult result,
                             Model model) {
        log.info("POST request to update ToDo id: {}", todoId);
        if (result.hasErrors()) {
            log.warn("Validation failed for ToDo update id {}: {}", todoId, result.getAllErrors());
            return "update-todo";
        }
        ToDo todo = todoService.readById(todoId);
        User owner = userService.readById(ownerId);
        todoDtoConverter.fillFields(todo, todoDto, owner);
        todoService.update(todo);
        log.info("ToDo id {} updated successfully", todoId);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @PostMapping("/{todo_id}/delete/users/{owner_id}")
    public String delete(@PathVariable("todo_id") Long todoId,
                         @PathVariable("owner_id") Long ownerId) {
        log.info("POST request to delete ToDo id: {}", todoId);
        todoService.delete(todoId);
        log.info("ToDo id {} deleted successfully", todoId);
        return "redirect:/todos/all/users/" + ownerId;
    }

    @GetMapping("/all/users/{user_id}")
    public String getAll(@PathVariable("user_id") Long userId, Model model) {
        log.info("GET request to list ToDos for user id: {}", userId);
        userService.readById(userId);
        List<ToDo> todos = todoService.getByUserId(userId);
        model.addAttribute("todos", todos);
        model.addAttribute("user", userService.readById(userId));
        return "todos-user";
    }

    @GetMapping("/{id}/tasks")
    public String getTasks(@PathVariable("id") Long todoId, Model model) {
        log.info("GET request to list tasks for ToDo id: {}", todoId);
        ToDo todo = todoService.readById(todoId);
        model.addAttribute("todo", todo);
        model.addAttribute("tasks", todo.getTasks());
        model.addAttribute("users", userService.getAll().stream()
                .filter(user -> !todo.getOwner().equals(user) && !todo.getCollaborators().contains(user))
                .collect(Collectors.toList()));
        return "todo-tasks";
    }

    @PostMapping("/{id}/add")
    public String addCollaborator(@PathVariable("id") Long todoId,
                                  @RequestParam("user_id") Long userId) {
        log.info("POST request to add collaborator id {} to ToDo id {}", userId, todoId);
        todoService.addCollaborator(todoId, userId);
        return "redirect:/todos/" + todoId + "/tasks";
    }

    @PostMapping("/{id}/remove")
    public String removeCollaborator(@PathVariable("id") Long todoId,
                                     @RequestParam("user_id") Long userId) {
        log.info("POST request to remove collaborator id {} from ToDo id {}", userId, todoId);
        todoService.removeCollaborator(todoId, userId);
        return "redirect:/todos/" + todoId + "/tasks";
    }
}