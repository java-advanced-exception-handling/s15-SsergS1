package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.State;
import com.softserve.itacademy.service.StateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StateController.class)
public class StateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StateService stateService;

    @Test
    void list_ShouldReturnStateListView() throws Exception {
        when(stateService.getAll()).thenReturn(Arrays.asList(new State()));

        mockMvc.perform(get("/states"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/state-list"))
                .andExpect(model().attributeExists("states"));
    }

    @Test
    void createGet_ShouldReturnCreateStateView() throws Exception {
        mockMvc.perform(get("/states/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/create-state"))
                .andExpect(model().attributeExists("state"));
    }

    @Test
    void createPost_ShouldRedirect_WhenValid() throws Exception {
        mockMvc.perform(post("/states/create")
                .param("name", "NewState"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/states"));

        verify(stateService).create(any(State.class));
    }

    @Test
    void createPost_ShouldReturnForm_WhenInvalid() throws Exception {
        mockMvc.perform(post("/states/create")
                .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("state/create-state"));
    }

    @Test
    void updateGet_ShouldReturnUpdateStateView() throws Exception {
        State state = new State();
        state.setId(1L);
        state.setName("OldName");
        when(stateService.readById(1L)).thenReturn(state);

        mockMvc.perform(get("/states/1/update"))
                .andExpect(status().isOk())
                .andExpect(view().name("state/update-state"))
                .andExpect(model().attribute("state", state));
    }

    @Test
    void delete_ShouldRedirect() throws Exception {
        mockMvc.perform(post("/states/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/states"));

        verify(stateService).delete(1L);
    }
}
