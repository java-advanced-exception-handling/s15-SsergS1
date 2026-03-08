package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.State;
import com.softserve.itacademy.service.StateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/states")
@RequiredArgsConstructor
@Slf4j
public class StateController {

    private final StateService stateService;

    @GetMapping
    public String list(Model model) {
        log.debug("GET request to list all states");
        model.addAttribute("states", stateService.getAll());
        return "state/state-list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        log.debug("GET request for state creation form");
        model.addAttribute("state", new State());
        return "state/create-state";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("state") State state,
                        BindingResult bindingResult) {
        log.info("POST request to create state: {}", state.getName());
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for state creation: {}", bindingResult.getAllErrors());
            return "state/create-state";
        }

        try {
            stateService.create(state);
            log.debug("State created successfully: {}", state.getName());
        } catch (IllegalArgumentException e) {
            log.warn("Error creating state: {}", e.getMessage());
            bindingResult.rejectValue("name", "error.state", e.getMessage());
            return "state/create-state";
        }

        return "redirect:/states";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable("id") Long id, Model model) {
        log.debug("GET request for state update form, ID: {}", id);
        State state = stateService.readById(id);
        model.addAttribute("state", state);
        return "state/update-state";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") Long id,
                        @Valid @ModelAttribute("state") State state,
                        BindingResult bindingResult) {
        log.info("POST request to update state ID: {}", id);
        if (bindingResult.hasErrors()) {
            log.warn("Validation failed for state update ID {}: {}", id, bindingResult.getAllErrors());
            return "state/update-state";
        }

        try {
            state.setId(id);
            stateService.update(state);
            log.debug("State ID {} updated successfully", id);
        } catch (IllegalArgumentException e) {
            log.warn("Error updating state ID {}: {}", id, e.getMessage());
            bindingResult.rejectValue("name", "error.state", e.getMessage());
            return "state/update-state";
        }

        return "redirect:/states";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") Long id) {
        log.info("POST request to delete state ID: {}", id);
        stateService.delete(id);
        log.debug("State ID {} deleted successfully", id);
        return "redirect:/states";
    }
}
