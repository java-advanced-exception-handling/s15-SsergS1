package com.softserve.itacademy.service;

import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.repository.StateRepository;
import com.softserve.itacademy.repository.ToDoRepository;
import com.softserve.itacademy.config.exception.NullEntityReferenceException;
import com.softserve.itacademy.repository.TaskRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {

    private final TaskRepository taskRepository;
    private final ToDoRepository toDoRepository;
    private final StateRepository stateRepository;
    private final TaskTransformer taskTransformer;

    private static final String DEFAULT_STATE = "New";
    private static final String DEFAULT_STATE_ALT = "NEW";

    @Transactional
    public TaskDto create(TaskDto taskDto) {
        log.info("Creating a new task with name: {} for ToDo ID: {}", taskDto.getName(), taskDto.getTodoId());
        if (taskRepository.existsByNameAndTodoId(taskDto.getName(), taskDto.getTodoId())) {
            log.warn("Task with name '{}' already exists in ToDo list {}", taskDto.getName(), taskDto.getTodoId());
            throw new IllegalArgumentException("Task with name '" + taskDto.getName() + "' already exists in this To-Do list");
        }
        Task task = taskTransformer.fillEntityFields(
                new Task(),
                taskDto,
                toDoRepository.findById(taskDto.getTodoId()).orElseThrow(() -> {
                    log.error("ToDo with ID {} not found", taskDto.getTodoId());
                    return new EntityNotFoundException("ToDo with id " + taskDto.getTodoId() + " not found");
                }),
                stateRepository.findByName(DEFAULT_STATE)
                        .or(() -> stateRepository.findByName(DEFAULT_STATE_ALT))
                        .orElseThrow(() -> {
                            log.error("Default state '{}' or '{}' not found", DEFAULT_STATE, DEFAULT_STATE_ALT);
                            return new EntityNotFoundException("State '" + DEFAULT_STATE + "' not found");
                        })
        );

        if (task != null) {
            Task savedTask = taskRepository.save(task);
            log.debug("Task saved with ID: {}", savedTask.getId());
            return taskTransformer.convertToDto(savedTask);
        }
        log.error("Attempted to create a null task");
        throw new NullEntityReferenceException("Task cannot be 'null'");
    }

    @Transactional(readOnly = true)
    public Task readById(long id) {
        log.debug("Reading task by ID: {}", id);
        return taskRepository.findById(id).orElseThrow(
                () -> {
                    log.error("Task with ID {} not found", id);
                    return new EntityNotFoundException("Task with id " + id + " not found");
                });
    }

    @Transactional
    public Task update(Task task) {
        log.info("Updating task with ID: {}", task.getId());
        if (task != null) {
            if (taskRepository.existsByNameAndTodoIdAndIdNot(task.getName(), task.getTodo().getId(), task.getId())) {
                log.warn("Task with name '{}' already exists in ToDo list {} (excluding task {})", 
                        task.getName(), task.getTodo().getId(), task.getId());
                throw new IllegalArgumentException("Task with name '" + task.getName() + "' already exists in this To-Do list");
            }
            readById(task.getId());
            Task updatedTask = taskRepository.save(task);
            log.debug("Task with ID {} successfully updated", updatedTask.getId());
            return updatedTask;
        }
        log.error("Attempted to update a null task");
        throw new NullEntityReferenceException("Task cannot be 'null'");
    }

    @Transactional
    public void delete(long id) {
        log.info("Deleting task with ID: {}", id);
        Task task = readById(id);
        taskRepository.delete(task);
        log.debug("Task with ID {} successfully deleted", id);
    }

    @Transactional(readOnly = true)
    public List<Task> getAll() {
        log.debug("Fetching all tasks");
        return taskRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Task> getByTodoId(long todoId) {
        log.debug("Fetching all tasks for ToDo ID: {}", todoId);
        return taskRepository.findByTodoId(todoId);
    }
}