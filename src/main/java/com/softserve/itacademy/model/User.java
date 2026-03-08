package com.softserve.itacademy.model;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = {"email"})
@ToString(exclude = {"myTodos", "otherTodos"})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "The 'first_name' cannot be empty")
    @Pattern(regexp = "[A-Z][a-z]+",
            message = "Must start with a capital letter followed by one or more lowercase letters")
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotBlank(message = "The 'last_name' cannot be empty")
    @Pattern(regexp = "[A-Z][a-z]+",
            message = "Must start with a capital letter followed by one or more lowercase letters")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotBlank(message = "The 'email' cannot be empty")
    @Pattern(regexp = "[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}", message = "Must be a valid e-mail address")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "The 'password' cannot be empty")
    @Column(name = "password", nullable = false)
    private String password;

    @NotNull(message = "The 'role' cannot be null")
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.REMOVE)
    private List<ToDo> myTodos;

    @ManyToMany
    @JoinTable(name = "todo_collaborator",
        joinColumns = @JoinColumn(name = "collaborator_id"),
        inverseJoinColumns = @JoinColumn(name = "todo_id"))
    private List<ToDo> otherTodos;

}
