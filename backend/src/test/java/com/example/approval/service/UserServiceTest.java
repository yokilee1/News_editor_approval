package com.example.approval.service;

import com.example.approval.dto.UserDto;
import com.example.approval.model.User;
import com.example.approval.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setUsername("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setRole(User.Role.valueOf("EDITOR"));

        testUserDto = new UserDto();
        testUserDto.setUsername("testuser");
        testUserDto.setPassword("password");
        testUserDto.setUsername("测试用户");
        testUserDto.setEmail("test@example.com");
        testUserDto.setRole("EDITOR");
    }

    @Test
    void createUser_shouldReturnCreatedUser() {
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User createdUser = userService.createUser(testUserDto);

        assertNotNull(createdUser);
        assertEquals("测试用户", createdUser.getUsername());
        assertEquals("encodedPassword", createdUser.getPassword());
        assertEquals("EDITOR", createdUser.getRole().name());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void getAllUsers_shouldReturnAllUsers() {
        when(userRepository.findAll()).thenReturn(Arrays.asList(testUser));

        List<User> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("testuser", users.get(0).getUsername());
    }

    @Test
    void getUserById_shouldReturnUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUserById(1L);

        assertNotNull(foundUser);
        assertEquals("testuser", foundUser.getUsername());
    }

    @Test
    void getUserByUsername_shouldReturnUser() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));

        User foundUser = userService.getUserByUsername("testuser");

        assertNotNull(foundUser);
        assertEquals(1L, foundUser.getId());
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        testUserDto.setUsername("更新的用户名");
        User updatedUser = userService.updateUserInfo(1L, testUserDto);

        assertNotNull(updatedUser);
        assertEquals("更新的用户名", updatedUser.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void deleteUser_shouldDeleteUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        doNothing().when(userRepository).delete(any(User.class));

        userService.deleteUser(1L);

        verify(userRepository, times(1)).delete(any(User.class));
    }

    @Test
    void getUsersByRole_shouldReturnUsersByRole() {
        when(userRepository.findByRole("EDITOR")).thenReturn(Arrays.asList(testUser));

        List<User> users = userService.getUsersByRole("EDITOR");

        assertNotNull(users);
        assertEquals(1, users.size());
        assertEquals("EDITOR", users.get(0).getRole());
    }
} 