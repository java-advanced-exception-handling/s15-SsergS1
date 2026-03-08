package com.softserve.itacademy.controller;

import com.softserve.itacademy.model.User;
import com.softserve.itacademy.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void login_ShouldReturnLoginPage_WhenNotLoggedIn() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void login_ShouldRedirectHome_WhenAlreadyLoggedIn() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user_id", 1L);

        mockMvc.perform(get("/login").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    void loginPost_ShouldRedirectHome_WhenCredentialsAreValid() throws Exception {
        User user = new User();
        user.setId(1L);
        user.setEmail("ivan@mail.com");
        user.setPassword("{noop}password");
        user.setFirstName("Ivan");

        when(userService.findByUsername("ivan@mail.com")).thenReturn(Optional.of(user));

        mockMvc.perform(post("/login")
                        .param("username", "ivan@mail.com")
                        .param("password", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(result -> {
                    assert result.getRequest().getSession().getAttribute("user_id").equals(1L);
                    assert result.getRequest().getSession().getAttribute("username").equals("Ivan");
                });
    }

    @Test
    void loginPost_ShouldRedirectWithErrorMessage_WhenCredentialsAreInvalid() throws Exception {
        when(userService.findByUsername("wrong@mail.com")).thenReturn(Optional.empty());

        mockMvc.perform(post("/login")
                        .param("username", "wrong@mail.com")
                        .param("password", "wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?error=true"));
    }

    @Test
    void logout_ShouldInvalidateSessionAndRedirect() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("user_id", 1L);

        mockMvc.perform(post("/logout").session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login?logout=true"))
                .andExpect(result -> {
                    assert session.isInvalid();
                });
    }
}
