package com.softserve.itacademy.repository;

import com.softserve.itacademy.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByTodoId(Long todoId);
    
    boolean existsByNameAndTodoId(String name, Long todoId);
    
    boolean existsByNameAndTodoIdAndIdNot(String name, Long todoId, Long taskId);

}
