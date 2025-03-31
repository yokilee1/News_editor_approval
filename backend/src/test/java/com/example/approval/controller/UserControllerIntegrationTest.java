package com.example.approval.controller;

import com.example.approval.dto.UserDto;
import com.example.approval.model.User;
import com.example.approval.service.LogService;
import com.example.approval.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.context.annotation.Import;
import com.example.approval.config.TestConfig;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestConfig.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private LogService logService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setRole(User.Role.valueOf("EDITOR"));

        testUserDto = new UserDto();
        testUserDto.setUsername("testuser");
        testUserDto.setPassword("password");
        testUserDto.setUsername("testuser");
        testUserDto.setEmail("test@example.com");
        testUserDto.setRole(User.Role.valueOf("EDITOR").name());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void createUser_shouldReturnCreatedUser() throws Exception {
        when(userService.createUser(any(UserDto.class))).thenReturn(testUser);
        when(logService.recordLog(anyString(), anyString(), anyString(), anyString(), anyLong(), any(), anyString())).thenReturn(null);

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto))
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    void getAllUsers_shouldReturnUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList(testUser));


        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].username").value("testuser"));
    }

    @Test
    void getUserById_shouldReturnUser() throws Exception {
        when(userService.getUserById(1L)).thenReturn(testUser);


        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        when(userService.updateUserInfo(anyLong(), any(UserDto.class))).thenReturn(testUser);
        when(logService.recordLog(anyString(), anyString(), anyString(), anyString(), anyLong(), any(), anyString())).thenReturn(null);

        mockMvc.perform(put("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUserDto))
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.user.username").value("testuser"));
    }

    @Test
    void deleteUser_shouldReturnSuccess() throws Exception {
        doNothing().when(userService).deleteUser(anyLong());
        when(logService.recordLog(anyString(), anyString(), anyString(), anyString(), anyLong(), any(), anyString())).thenReturn(null);

        mockMvc.perform(delete("/api/users/1")
                .header("X-User-ID", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void getUsersByRole_shouldReturnUsersByRole() throws Exception {
        when(userService.getUsersByRole("EDITOR")).thenReturn(Arrays.asList(testUser));

        mockMvc.perform(get("/api/users/role/EDITOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].role").value("EDITOR"));
    }
} 