package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worktrack.dto.request.LoginRequest;
import com.worktrack.dto.request.LogoutRequest;
import com.worktrack.dto.request.RefreshTokenRequest;
import com.worktrack.dto.request.RegisterRequest;
import com.worktrack.dto.response.AuthResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthenticationControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authenticationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/auth/register should return 200 and AuthResponse")
    void register_ReturnsAuthResponse() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");
        request.setPhone("1234567890");
        request.setCompanyId(1L);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("access-token-xyz")
                .refreshToken("refresh-token-xyz")
                .tokenType("Bearer")
                .build();

        when(authenticationService.register(any(RegisterRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token-xyz"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token-xyz"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login should return 200 and AuthResponse")
    void login_ReturnsAuthResponse() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("john.doe@example.com");
        request.setPassword("password123");

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("access-token-xyz")
                .refreshToken("refresh-token-xyz")
                .tokenType("Bearer")
                .build();

        when(authenticationService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("access-token-xyz"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/refresh should return 200 and rotated AuthResponse")
    void refreshToken_ReturnsNewAuthResponse() throws Exception {
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("old-refresh-token")
                .deviceId("device-1")
                .build();

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken("new-access-token")
                .refreshToken("new-refresh-token")
                .tokenType("Bearer")
                .build();

        when(authenticationService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/logout should return 200 OK")
    void logout_ReturnsSuccess() throws Exception {
        LogoutRequest request = LogoutRequest.builder()
                .refreshToken("logout-refresh-token")
                .build();

        doNothing().when(authenticationService).logout(any(LogoutRequest.class));

        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Logged out successfully"));
    }
}
