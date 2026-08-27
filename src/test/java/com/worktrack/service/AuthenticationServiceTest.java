package com.worktrack.service;

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
import com.worktrack.serviceImpl.AuthenticationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthenticationServiceImpl authenticationService;

    private User sampleUser;
    private Company sampleCompany;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authenticationService, "refreshExpiration", 604800000L);

        sampleCompany = Company.builder()
                .name("Acme Corp")
                .build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .phone("1234567890")
                .role(UserRole.ADMIN)
                .company(sampleCompany)
                .enabled(true)
                .build();
        ReflectionTestUtils.setField(sampleUser, "id", 10L);
    }

    private RegisterRequest createRegisterRequest(String email, Long companyId) {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail(email);
        request.setPassword("password123");
        request.setPhone("1234567890");
        request.setCompanyId(companyId);
        return request;
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Should successfully register user and return tokens")
        void register_Success() {
            RegisterRequest request = createRegisterRequest("john.doe@example.com", 1L);

            when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
            when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(userRepository.save(any(User.class))).thenReturn(sampleUser);
            when(jwtService.generateToken("john.doe@example.com")).thenReturn("access-token-123");
            when(jwtService.generateRefreshToken("john.doe@example.com")).thenReturn("refresh-token-123");

            AuthResponse response = authenticationService.register(request);

            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("access-token-123");
            assertThat(response.getRefreshToken()).isEqualTo("refresh-token-123");
            assertThat(response.getTokenType()).isEqualTo("Bearer");

            verify(refreshTokenRepository, times(1)).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("Should throw EmailAlreadyExistsException when email already registered")
        void register_DuplicateEmail() {
            RegisterRequest request = createRegisterRequest("john.doe@example.com", 1L);

            when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(true);

            assertThatThrownBy(() -> authenticationService.register(request))
                    .isInstanceOf(EmailAlreadyExistsException.class)
                    .hasMessageContaining("Email already exists");
        }

        @Test
        @DisplayName("Should throw CompanyNotFoundException when company ID invalid")
        void register_InvalidCompany() {
            RegisterRequest request = createRegisterRequest("john.doe@example.com", 99L);

            when(userRepository.existsByEmail("john.doe@example.com")).thenReturn(false);
            when(companyRepository.findById(99L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> authenticationService.register(request))
                    .isInstanceOf(CompanyNotFoundException.class)
                    .hasMessageContaining("Company not found");
        }
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Should successfully login user and issue fresh tokens")
        void login_Success() {
            LoginRequest request = new LoginRequest();
            request.setEmail("john.doe@example.com");
            request.setPassword("password123");

            when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Optional.of(sampleUser));
            when(jwtService.generateToken("john.doe@example.com")).thenReturn("new-access-token");
            when(jwtService.generateRefreshToken("john.doe@example.com")).thenReturn("new-refresh-token");

            AuthResponse response = authenticationService.login(request);

            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("new-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
            verify(authenticationManager).authenticate(any());
            verify(refreshTokenRepository).save(any(RefreshToken.class));
        }
    }

    @Nested
    @DisplayName("Refresh Token Rotation Tests")
    class RefreshTokenTests {

        @Test
        @DisplayName("Should rotate refresh token successfully when valid")
        void refreshToken_Success() {
            RefreshTokenRequest request = RefreshTokenRequest.builder()
                    .refreshToken("valid-refresh-token")
                    .deviceId("device-1")
                    .build();

            RefreshToken validToken = RefreshToken.builder()
                    .id(1L)
                    .user(sampleUser)
                    .tokenHash("hash-value")
                    .expiresAt(LocalDateTime.now().plusDays(1))
                    .revoked(false)
                    .build();

            when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(validToken));
            when(jwtService.generateToken("john.doe@example.com")).thenReturn("rotated-access-token");
            when(jwtService.generateRefreshToken("john.doe@example.com")).thenReturn("rotated-refresh-token");

            AuthResponse response = authenticationService.refreshToken(request);

            assertThat(response).isNotNull();
            assertThat(response.getAccessToken()).isEqualTo("rotated-access-token");
            assertThat(response.getRefreshToken()).isEqualTo("rotated-refresh-token");
            assertThat(validToken.getRevoked()).isTrue();
            verify(refreshTokenRepository, times(2)).save(any(RefreshToken.class));
        }

        @Test
        @DisplayName("Should detect token reuse and revoke all user tokens")
        void refreshToken_ReuseDetected_RevokesAllTokens() {
            RefreshTokenRequest request = RefreshTokenRequest.builder()
                    .refreshToken("reused-refresh-token")
                    .build();

            RefreshToken revokedToken = RefreshToken.builder()
                    .id(1L)
                    .user(sampleUser)
                    .tokenHash("reused-hash")
                    .expiresAt(LocalDateTime.now().plusDays(1))
                    .revoked(true)
                    .build();

            when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(revokedToken));

            assertThatThrownBy(() -> authenticationService.refreshToken(request))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("Revoked refresh token reused");

            verify(refreshTokenRepository).revokeAllByUser(sampleUser);
        }

        @Test
        @DisplayName("Should throw exception when refresh token is expired")
        void refreshToken_Expired() {
            RefreshTokenRequest request = RefreshTokenRequest.builder()
                    .refreshToken("expired-token")
                    .build();

            RefreshToken expiredToken = RefreshToken.builder()
                    .id(1L)
                    .user(sampleUser)
                    .tokenHash("expired-hash")
                    .expiresAt(LocalDateTime.now().minusHours(1))
                    .revoked(false)
                    .build();

            when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(expiredToken));

            assertThatThrownBy(() -> authenticationService.refreshToken(request))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessageContaining("expired");

            assertThat(expiredToken.getRevoked()).isTrue();
        }
    }

    @Nested
    @DisplayName("Logout Tests")
    class LogoutTests {

        @Test
        @DisplayName("Should revoke refresh token on logout")
        void logout_Success() {
            LogoutRequest request = LogoutRequest.builder()
                    .refreshToken("logout-token")
                    .build();

            RefreshToken activeToken = RefreshToken.builder()
                    .id(1L)
                    .user(sampleUser)
                    .revoked(false)
                    .build();

            when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(activeToken));

            authenticationService.logout(request);

            assertThat(activeToken.getRevoked()).isTrue();
            verify(refreshTokenRepository).save(activeToken);
        }
    }
}
