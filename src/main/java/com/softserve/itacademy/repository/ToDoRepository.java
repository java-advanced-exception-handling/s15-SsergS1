package com.softserve.itacademy.repository;

import com.softserve.itacademy.model.ToDo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToDoRepository extends JpaRepository<ToDo, Long> {

    @Query("SELECT DISTINCT t FROM ToDo t LEFT JOIN FETCH t.collaborators " +
           "WHERE t.owner.id = :userId OR :userId IN (SELECT c.id FROM t.collaborators c)")
    List<ToDo> getByUserId(Long userId);

    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);

}
