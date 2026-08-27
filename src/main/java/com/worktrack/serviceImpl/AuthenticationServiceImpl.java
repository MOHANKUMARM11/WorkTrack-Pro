package com.worktrack.serviceImpl;

import com.worktrack.constants.UserRole;
import com.worktrack.dto.request.LoginRequest;
import com.worktrack.dto.request.LogoutRequest;
import com.worktrack.dto.request.RefreshTokenRequest;
import com.worktrack.dto.request.RegisterRequest;
import com.worktrack.dto.response.AuthResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.RefreshToken;
import com.worktrack.entity.User;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.exception.custom.EmailAlreadyExistsException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.RefreshTokenRepository;
import com.worktrack.repository.UserRepository;
import com.worktrack.security.jwt.JwtService;
import com.worktrack.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() ->
                        new CompanyNotFoundException("Company not found"));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .role(UserRole.ADMIN)
                .company(company)
                .enabled(true)
                .build();

        User savedUser = userRepository.save(user);

        String accessToken = jwtService.generateToken(savedUser.getEmail());
        String refreshToken = jwtService.generateRefreshToken(savedUser.getEmail());

        saveRefreshToken(savedUser, refreshToken, null);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));

        String accessToken = jwtService.generateToken(user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getEmail());

        saveRefreshToken(user, refreshToken, null);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        String rawToken = request.getRefreshToken();
        String tokenHash = hashToken(rawToken);

        RefreshToken storedToken = refreshTokenRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (Boolean.TRUE.equals(storedToken.getRevoked())) {
            // Token reuse detected! Revoke all tokens for this user as a security measure.
            refreshTokenRepository.revokeAllByUser(storedToken.getUser());
            throw new BadCredentialsException("Revoked refresh token reused. Session revoked for security.");
        }

        if (storedToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            storedToken.setRevoked(true);
            refreshTokenRepository.save(storedToken);
            throw new BadCredentialsException("Refresh token has expired");
        }

        User user = storedToken.getUser();

        // Revoke the old token (rotation)
        storedToken.setRevoked(true);
        refreshTokenRepository.save(storedToken);

        // Generate new token pair
        String newAccessToken = jwtService.generateToken(user.getEmail());
        String newRefreshToken = jwtService.generateRefreshToken(user.getEmail());

        saveRefreshToken(user, newRefreshToken, request.getDeviceId());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .tokenType("Bearer")
                .build();
    }

    @Override
    public void logout(LogoutRequest request) {

        String tokenHash = hashToken(request.getRefreshToken());

        refreshTokenRepository.findByTokenHash(tokenHash).ifPresent(token -> {
            if (!Boolean.TRUE.equals(token.getRevoked())) {
                token.setRevoked(true);
                refreshTokenRepository.save(token);
            }
        });
    }

    private void saveRefreshToken(User user, String rawRefreshToken, String deviceId) {
        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .tokenHash(hashToken(rawRefreshToken))
                .deviceId(deviceId)
                .issuedAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpiration / 1000))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshTokenEntity);
    }

    private String hashToken(String token) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(token.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}