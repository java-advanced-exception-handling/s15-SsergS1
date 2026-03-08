package com.softserve.itacademy.service;

import com.softserve.itacademy.dto.TaskDto;
import com.softserve.itacademy.dto.TaskTransformer;
import com.softserve.itacademy.model.State;
import com.softserve.itacademy.model.Task;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.repository.StateRepository;
import com.softserve.itacademy.repository.TaskRepository;
import com.softserve.itacademy.repository.ToDoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ToDoRepository toDoRepository;
    @Mock
    private StateRepository stateRepository;
    @Mock
    private TaskTransformer taskTransformer;

    @InjectMocks
    private TaskService taskService;

    private TaskDto taskDto;
    private Task task;
    private ToDo todo;
    private State state;

    @BeforeEach
    void setUp() {
        todo = new ToDo();
        todo.setId(1L);

        state = new State();
        state.setId(1L);
        state.setName("New");

        taskDto = new TaskDto();
        taskDto.setName("Test Task");
        taskDto.setTodoId(1L);

        task = new Task();
        task.setId(1L);
        task.setName("Test Task");
        task.setTodo(todo);
    }

    @Test
    void create_ShouldReturnSavedTask_WhenValid() {
        when(taskRepository.existsByNameAndTodoId(anyString(), anyLong())).thenReturn(false);
        when(toDoRepository.findById(1L)).thenReturn(Optional.of(todo));
        when(stateRepository.findByName("New")).thenReturn(Optional.of(state));
        when(taskTransformer.fillEntityFields(any(), any(), any(), any())).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskTransformer.convertToDto(any())).thenReturn(taskDto);

        TaskDto result = taskService.create(taskDto);

        assertNotNull(result);
        assertEquals("Test Task", result.getName());
        verify(taskRepository).save(any());
    }

    @Test
    void create_ShouldThrowException_WhenTaskNameExistsInTodo() {
        when(taskRepository.existsByNameAndTodoId(anyString(), anyLong())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> taskService.create(taskDto));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void readById_ShouldReturnTask_WhenExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        Task result = taskService.readById(1L);

        assertEquals(task, result);
    }

    @Test
    void readById_ShouldThrowException_WhenNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> taskService.readById(1L));
    }

    @Test
    void update_ShouldReturnUpdatedTask_WhenValid() {
        when(taskRepository.existsByNameAndTodoIdAndIdNot(anyString(), anyLong(), anyLong())).thenReturn(false);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task updatedTask = taskService.update(task);

        assertEquals(task, updatedTask);
    }

    @Test
    void delete_ShouldCallRepository_WhenExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        taskService.delete(1L);

        verify(taskRepository).delete(task);
    }
}
