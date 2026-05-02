package com.krishanu.inventory.authentication.controller;

import com.krishanu.inventory.common.security.JwtUtils;
import com.krishanu.inventory.common.security.LoginRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtils jwtUtil;

    @InjectMocks
    private AuthController authController;

    @Test
    void shouldReturnTokenOnValidLogin() {

        LoginRequest request = new LoginRequest("admin", "password");

        when(jwtUtil.generateToken(anyString(), anyString()))
                .thenReturn("mock-token");

        ResponseEntity<?> response = authController.login(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}