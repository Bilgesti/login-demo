package com.example.login;

import com.example.login.model.User;
import com.example.login.model.UserDTO;
import com.example.login.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
public class ControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {

        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");
        mockUser.setPassword("password");
        mockUser.setRole("ROLE_USER");

        Mockito.when(userService.registerUser(Mockito.any(UserDTO.class))).thenReturn(mockUser);
        Mockito.when(userService.findAll()).thenReturn(List.of(mockUser));
        Mockito.doNothing().when(userService).deleteUser(Mockito.anyString());
        Mockito.doThrow(new RuntimeException("User not found")).when(userService).deleteUser("nonexistent@example.com");
    }

    @Test
    public void testHomepageWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isFound());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    public void testHomepageWithAuthentication() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testSuccessfulRegistration() throws Exception {
        mockMvc.perform(post("/register")
                        .param("email", "test@example.com")
                        .param("password", "asdasd"))
                .andExpect(status().isOk())
                .andExpect(view().name("register_success"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testFailedRegistration() throws Exception {
        mockMvc.perform(post("/register")
                        .param("email", "invalid-email")
                        .param("password", "short"))
                .andExpect(status().isOk())
                .andExpect(view().name("register_form"))
                .andExpect(model().attributeHasFieldErrors("user", "email", "password"));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    public void testSuccessfulDeletion() throws Exception {
        mockMvc.perform(post("/delete")
                        .param("email", "user@example.com"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/delete_success"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    public void testFailedDeletion() throws Exception {
        mockMvc.perform(post("/delete")
                        .param("email", "nonexistent@example.com"))
                .andExpect(status().isOk())
                .andExpect(view().name("delete_error"))
                .andExpect(model().attributeExists("error"));
    }
}
