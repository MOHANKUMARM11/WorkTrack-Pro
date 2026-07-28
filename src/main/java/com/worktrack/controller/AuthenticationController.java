package com.worktrack.controller;

import com.worktrack.common.response.ApiResponse;
import com.worktrack.dto.request.RegisterRequest;
import com.worktrack.dto.response.AuthResponse;
import com.worktrack.service.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.worktrack.dto.request.LoginRequest;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ApiResponse<AuthResponse> register(
            @Valid @RequestBody RegisterRequest request) {

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("User registered successfully")
                .data(authenticationService.register(request))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(
            @Valid @RequestBody LoginRequest request) {

        return ApiResponse.<AuthResponse>builder()
                .success(true)
                .message("Login successful")
                .data(authenticationService.login(request))
                .build();
    }
}