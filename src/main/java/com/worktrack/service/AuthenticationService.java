package com.worktrack.service;

import com.worktrack.dto.request.LoginRequest;
import com.worktrack.dto.request.LogoutRequest;
import com.worktrack.dto.request.RefreshTokenRequest;
import com.worktrack.dto.request.RegisterRequest;
import com.worktrack.dto.response.AuthResponse;

public interface AuthenticationService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    AuthResponse refreshToken(RefreshTokenRequest request);

    void logout(LogoutRequest request);
}