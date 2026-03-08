package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.model.TaskPriority;
import com.softserve.itacademy.service.TaskService;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.service.StateService;
import com.softserve.itacademy.service.ToDoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Slf4j
public class TaskController {

    private final TaskService taskService;
    private final ToDoService todoService;
    private final StateService stateService;
    private final TaskTransformer taskTransformer;

    @GetMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") Long todoId, Model model) {
        log.debug("GET request for task creation form in ToDo ID: {}", todoId);
        TaskDto taskDto = TaskDto.builder()
                .todoId(todoId)
                .build();

        model.addAttribute("task", taskDto);
        model.addAttribute("todo", todoService.readById(todoId));
        model.addAttribute("priorities", TaskPriority.values());

        return "create-task";
    }

    @PostMapping("/create/todos/{todo_id}")
    public String create(@PathVariable("todo_id") Long todoId,
                        @Valid @ModelAttribute("task") TaskDto taskDto,
                        BindingResult bindingResult,
                        Model model) {
        log.info("POST request to create task '{}' in ToDo ID: {}", taskDto.getName(), todoId);
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for task creation: {}", bindingResult.getAllErrors());
            model.addAttribute("todo", todoService.readById(todoId));
            model.addAttribute("priorities", TaskPriority.values());
            return "create-task";
        }

        try {
            taskDto.setTodoId(todoId);
            taskService.create(taskDto);
            log.debug("Task '{}' created successfully", taskDto.getName());
        } catch (IllegalArgumentException e) {
            log.warn("Error creating task: {}", e.getMessage());
            bindingResult.rejectValue("name", "error.task", e.getMessage());
            model.addAttribute("todo", todoService.readById(todoId));
            model.addAttribute("priorities", TaskPriority.values());
            return "create-task";
        }

        return "redirect:/todos/" + todoId + "/tasks";
    }

    @GetMapping("/{task_id}/update/todos/{todo_id}")
    public String taskUpdateForm(@PathVariable("task_id") Long taskId,
                                 @PathVariable("todo_id") Long todoId,
                                 Model model) {
        log.debug("GET request for task update form, ID: {}, ToDo ID: {}", taskId, todoId);
        Task task = taskService.readById(taskId);
        TaskDto taskDto = taskTransformer.convertToDto(task);

        model.addAttribute("task", taskDto);
        model.addAttribute("todo", todoService.readById(todoId));
        model.addAttribute("priorities", TaskPriority.values());
        model.addAttribute("states", stateService.getAll());

        return "update-task";
    }

    @PostMapping("/{task_id}/update/todos/{todo_id}")
    public String update(@PathVariable("task_id") Long taskId,
                        @PathVariable("todo_id") Long todoId,
                        @Valid @ModelAttribute("task") TaskDto taskDto,
                        BindingResult bindingResult,
                        Model model) {
        log.info("POST request to update task ID: {}, ToDo ID: {}", taskId, todoId);
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for task update ID {}: {}", taskId, bindingResult.getAllErrors());
            model.addAttribute("todo", todoService.readById(todoId));
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("states", stateService.getAll());
            return "update-task";
        }

        try {
            Task task = taskService.readById(taskId);
            Task updatedTask = taskTransformer.fillEntityFields(
                    task,
                    taskDto,
                    todoService.readById(todoId),
                    stateService.readById(taskDto.getStateId())
            );

            taskService.update(updatedTask);
            log.debug("Task ID {} updated successfully", taskId);
        } catch (IllegalArgumentException e) {
            log.warn("Error updating task ID {}: {}", taskId, e.getMessage());
            bindingResult.rejectValue("name", "error.task", e.getMessage());
            model.addAttribute("todo", todoService.readById(todoId));
            model.addAttribute("priorities", TaskPriority.values());
            model.addAttribute("states", stateService.getAll());
            return "update-task";
        }

        return "redirect:/todos/" + todoId + "/tasks";
    }

    @PostMapping("/{task_id}/delete/todos/{todo_id}")
    public String delete(@PathVariable("task_id") Long taskId,
                        @PathVariable("todo_id") Long todoId) {
        log.info("POST request to delete task ID: {}, ToDo ID: {}", taskId, todoId);
        taskService.delete(taskId);
        log.debug("Task ID {} deleted successfully", taskId);
        return "redirect:/todos/" + todoId + "/tasks";
    }
}
