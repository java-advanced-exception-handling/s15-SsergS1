package com.softserve.itacademy.service;

import com.softserve.itacademy.model.State;
import com.softserve.itacademy.config.exception.NullEntityReferenceException;
import com.softserve.itacademy.dto.StateDto;
import com.softserve.itacademy.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StateService {

    private final StateRepository stateRepository;

    @Transactional
    public State create(State state) {
        log.info("Creating a new state with name: {}", state.getName());
        if (state != null) {
            if (stateRepository.existsByName(state.getName())) {
                log.warn("Attempted to create a state that already exists: {}", state.getName());
                throw new IllegalArgumentException("State with name '" + state.getName() + "' already exists");
            }
            State savedState = stateRepository.save(state);
            log.debug("State saved with ID: {}", savedState.getId());
            return savedState;
        }
        log.error("Attempted to create a null state");
        throw new NullEntityReferenceException("State cannot be 'null'");
    }

    @Transactional(readOnly = true)
    public State readById(long id) {
        log.debug("Reading state by ID: {}", id);
        return stateRepository.findById(id).orElseThrow(
                () -> {
                    log.error("State with ID {} not found", id);
                    return new EntityNotFoundException("State with id " + id + " not found");
                });
    }

    @Transactional
    public State update(State state) {
        log.info("Updating state with ID: {}", state.getId());
        if (state != null) {
            if (stateRepository.existsByNameAndIdNot(state.getName(), state.getId())) {
                log.warn("Attempted to update state {} to name {} which already exists", state.getId(), state.getName());
                throw new IllegalArgumentException("State with name '" + state.getName() + "' already exists");
            }
            readById(state.getId());
            State updatedState = stateRepository.save(state);
            log.debug("State with ID {} successfully updated", updatedState.getId());
            return updatedState;
        }
        log.error("Attempted to update a null state");
        throw new NullEntityReferenceException("State cannot be 'null'");
    }

    @Transactional
    public void delete(long id) {
        log.info("Deleting state with ID: {}", id);
        State state = readById(id);
        stateRepository.delete(state);
        log.debug("State with ID {} successfully deleted", id);
    }

    @Transactional(readOnly = true)
    public List<State> getAll() {
        log.debug("Fetching all states sorted by ID");
        return stateRepository.findAllByOrderByIdAsc();
    }

    @Transactional(readOnly = true)
    public State getByName(String name) {
        log.debug("Searching for state by name: {}", name);
        return stateRepository.findByName(name)
                .orElseThrow(() -> {
                    log.error("State with name '{}' not found", name);
                    return new EntityNotFoundException("State with name '" + name + "' not found");
                });
    }

    @Transactional(readOnly = true)
    public List<StateDto> findAll() {
        log.debug("Fetching all states as DTOs");
        return stateRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    private StateDto toDto(State state) {
        return StateDto.builder()
                .name(state.getName())
                .build();
    }
}
