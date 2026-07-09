package com.softserve.itacademy.service;

import com.softserve.itacademy.exception.NullEntityReferenceException;
import com.softserve.itacademy.model.ToDo;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.repository.ToDoRepository;
import com.softserve.itacademy.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ToDoService {
    private final ToDoRepository todoRepository;
    private final UserRepository userRepository;

    @Transactional
    public ToDo create(ToDo todo) {
        if (todo != null) {
            log.info("Creating ToDo with title: {}", todo.getTitle());
            if (todoRepository.existsByTitle(todo.getTitle())) {
                log.warn("Attempt to create ToDo with existing title: {}", todo.getTitle());
                throw new IllegalArgumentException("ToDo with title '" + todo.getTitle() + "' already exists");
            }
            return todoRepository.save(todo);
        }
        log.error("Null entity reference while creating ToDo");
        throw new NullEntityReferenceException("ToDo cannot be 'null'");
    }

    @Transactional(readOnly = true)
    public ToDo readById(long id) {
        log.info("Reading ToDo by id: {}", id);
        return todoRepository.findById(id).orElseThrow(
                () -> {
                    log.error("ToDo with id {} not found", id);
                    return new EntityNotFoundException("ToDo with id " + id + " not found");
                });
    }

    @Transactional
    public ToDo update(ToDo todo) {
        if (todo != null) {
            log.info("Updating ToDo with id: {}", todo.getId());
            if (todoRepository.existsByTitleAndIdNot(todo.getTitle(), todo.getId())) {
                log.warn("Attempt to update ToDo to an existing title: {}", todo.getTitle());
                throw new IllegalArgumentException("ToDo with title '" + todo.getTitle() + "' already exists");
            }
            readById(todo.getId());
            return todoRepository.save(todo);
        }
        log.error("Null entity reference while updating ToDo");
        throw new NullEntityReferenceException("ToDo cannot be 'null'");
    }

    @Transactional
    public void delete(long id) {
        log.info("Deleting ToDo with id: {}", id);
        ToDo todo = readById(id);
        todoRepository.delete(todo);
        log.info("ToDo {} deleted successfully", id);
    }

    @Transactional(readOnly = true)
    public List<ToDo> getAll() {
        return todoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<ToDo> getByUserId(long userId) {
        log.info("Getting ToDos for user id: {}", userId);
        return todoRepository.getByUserId(userId);
    }

    @Transactional
    public void addCollaborator(long todoId, long userId) {
        log.info("Adding collaborator user id: {} to ToDo id: {}", userId, todoId);
        ToDo todo = readById(todoId);
        User collaborator = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Collaborator user with id {} not found", userId);
                    return new EntityNotFoundException("User with id " + userId + " not found");
                });
        todo.getCollaborators().add(collaborator);
        update(todo);
        log.info("Collaborator {} added to ToDo {}", userId, todoId);
    }

    @Transactional
    public void removeCollaborator(long todoId, long userId) {
        log.info("Removing collaborator user id: {} from ToDo id: {}", userId, todoId);
        ToDo todo = readById(todoId);
        User collaborator = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("Collaborator user with id {} not found for removal", userId);
                    return new EntityNotFoundException("User with id " + userId + " not found");
                });
        todo.getCollaborators().remove(collaborator);
        update(todo);
        log.info("Collaborator {} removed from ToDo {}", userId, todoId);
    }
}
