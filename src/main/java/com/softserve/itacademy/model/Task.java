package com.softserve.itacademy.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"name", "priority", "todo"})
@ToString(exclude = {"todo", "state"})
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The 'name' cannot be empty")
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull(message = "The 'priority' cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private TaskPriority priority;

    @NotNull(message = "The 'todo' cannot be null")
    @ManyToOne
    @JoinColumn(name = "todo_id")
    private ToDo todo;

    @NotNull(message = "The 'state' cannot be null")
    @ManyToOne
    @JoinColumn(name = "state_id")
    private State state;

}
