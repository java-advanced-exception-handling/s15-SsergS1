package com.softserve.itacademy.service;

import com.softserve.itacademy.model.State;
import com.softserve.itacademy.repository.StateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StateServiceTest {

    @Mock
    private StateRepository stateRepository;

    @InjectMocks
    private StateService stateService;

    private State state;

    @BeforeEach
    void setUp() {
        state = new State();
        state.setId(1L);
        state.setName("New");
    }

    @Test
    void create_ShouldReturnSavedState_WhenStateIsUnique() {
        when(stateRepository.existsByName(state.getName())).thenReturn(false);
        when(stateRepository.save(any(State.class))).thenReturn(state);

        State savedState = stateService.create(state);

        assertNotNull(savedState);
        assertEquals(state.getName(), savedState.getName());
        verify(stateRepository).save(state);
    }

    @Test
    void create_ShouldThrowException_WhenStateNameExists() {
        when(stateRepository.existsByName(state.getName())).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> stateService.create(state));
        verify(stateRepository, never()).save(any());
    }

    @Test
    void readById_ShouldReturnState_WhenExists() {
        when(stateRepository.findById(1L)).thenReturn(Optional.of(state));

        State foundState = stateService.readById(1L);

        assertEquals(state, foundState);
    }

    @Test
    void readById_ShouldThrowException_WhenNotFound() {
        when(stateRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> stateService.readById(1L));
    }

    @Test
    void update_ShouldReturnUpdatedState_WhenValid() {
        when(stateRepository.existsByNameAndIdNot(state.getName(), 1L)).thenReturn(false);
        when(stateRepository.findById(1L)).thenReturn(Optional.of(state));
        when(stateRepository.save(state)).thenReturn(state);

        State updatedState = stateService.update(state);

        assertEquals(state, updatedState);
    }

    @Test
    void delete_ShouldCallRepository_WhenExists() {
        when(stateRepository.findById(1L)).thenReturn(Optional.of(state));

        stateService.delete(1L);

        verify(stateRepository).delete(state);
    }

    @Test
    void getAll_ShouldReturnList() {
        List<State> states = Arrays.asList(state);
        when(stateRepository.findAllByOrderByIdAsc()).thenReturn(states);

        List<State> result = stateService.getAll();

        assertEquals(1, result.size());
        assertEquals("New", result.get(0).getName());
    }
}
