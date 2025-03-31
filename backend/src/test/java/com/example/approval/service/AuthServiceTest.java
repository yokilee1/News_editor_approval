package com.example.approval.service;

import com.example.approval.dto.AuthRequest;
import com.example.approval.dto.AuthResponse;
import com.example.approval.model.User;
import com.example.approval.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private AuthRequest authRequest;
    private User testUser;
    private Authentication authentication;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        authRequest = new AuthRequest();
        authRequest.setUsername("testuser");
        authRequest.setPassword("password");

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setUsername("测试用户");
        testUser.setRole(User.Role.valueOf("EDITOR"));

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testuser")
                .password("encodedPassword")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_EDITOR")))
                .build();

        authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
    }

    @Test
    void authenticate_shouldReturnAuthResponse() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userDetailsService.loadUserByUsername("testuser")).thenReturn(userDetails);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        when(jwtService.generateToken(any(UserDetails.class))).thenReturn("test.jwt.token");

        AuthResponse response = authService.authenticate(authRequest);

        assertNotNull(response);
        assertEquals("test.jwt.token", response.getToken());
        assertEquals(1L, response.getUserId());
        assertEquals("testuser", response.getUsername());
        assertEquals("测试用户", response.getName());
        assertEquals("EDITOR", response.getRole());
    }

    @Test
    void validateToken_shouldReturnTrue() {
        when(jwtService.validateToken(anyString(), any(UserDetails.class))).thenReturn(true);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.extractUsername(anyString())).thenReturn("testuser");

        boolean result = authService.validateToken("test.jwt.token");

        assertTrue(result);
    }

    @Test
    void validateToken_shouldReturnFalse() {
        when(jwtService.validateToken(anyString(), any(UserDetails.class))).thenReturn(false);
        when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
        when(jwtService.extractUsername(anyString())).thenReturn("testuser");

        boolean result = authService.validateToken("invalid.jwt.token");

        assertFalse(result);
    }
} 