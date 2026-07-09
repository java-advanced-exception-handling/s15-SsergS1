package com.softserve.itacademy.controller;

import com.softserve.itacademy.dto.userDto.CreateUserDto;
import com.softserve.itacademy.dto.userDto.UpdateUserDto;
import com.softserve.itacademy.dto.userDto.UserDtoConverter;
import com.softserve.itacademy.model.User;
import com.softserve.itacademy.model.UserRole;
import com.softserve.itacademy.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserDtoConverter userDtoConverter;

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("user", new CreateUserDto());
        return "create-user";
    }

    @PostMapping("/create")
    public String create(@Validated @ModelAttribute("user") CreateUserDto userDto,
                         BindingResult result) {
        log.info("POST request to create user: {}", userDto.getEmail());
        if (result.hasErrors()) {
            log.warn("Validation failed for user creation: {}", result.getAllErrors());
            return "create-user";
        }
        User user = userService.register(userDto);
        log.info("User created successfully with id: {}", user.getId());
        return "redirect:/todos/all/users/" + user.getId();
    }

    @GetMapping("/{id}/read")
    public String read(@PathVariable("id") Long id, Model model) {
        log.info("GET request to read user with id: {}", id);
        User user = userService.readById(id);
        model.addAttribute("user", user);
        return "user-info";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable("id") Long id, Model model) {
        log.info("GET request to update form for user id: {}", id);
        User user = userService.readById(id);
        UpdateUserDto userDto = userDtoConverter.toUpdateDto(user);
        model.addAttribute("user", userDto);
        model.addAttribute("roles", UserRole.values());
        return "update-user";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                         @Validated @ModelAttribute("user") UpdateUserDto userDto,
                         BindingResult result,
                         Model model) {
        log.info("POST request to update user id: {}", id);
        if (result.hasErrors()) {
            log.warn("Validation failed for user update id {}: {}", id, result.getAllErrors());
            model.addAttribute("roles", UserRole.values());
            return "update-user";
        }
        userDto.setId(id);
        userService.update(userDto);
        log.info("User id {} updated successfully", id);
        return "redirect:/users/all";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        log.info("POST request to delete user id: {}", id);
        userService.delete(id);
        log.info("User id {} deleted successfully", id);
        return "redirect:/users/all";
    }

    @GetMapping("/all")
    public String getAll(Model model) {
        log.info("GET request to list all users");
        model.addAttribute("users", userService.getAll());
        return "users-list";
    }
}