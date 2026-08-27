package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.worktrack.dto.response.NotificationResponse;
import com.worktrack.entity.User;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NotificationControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationController notificationController;

    private User sampleUser;
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(notificationController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        sampleUser = User.builder().email("john@acme.com").build();
        ReflectionTestUtils.setField(sampleUser, "id", 100L);

        authentication = new UsernamePasswordAuthenticationToken(sampleUser, null, List.of());
    }

    @Test
    @DisplayName("GET /api/v1/notifications should return notifications list")
    void getNotifications_ReturnsList() throws Exception {
        NotificationResponse response = NotificationResponse.builder()
                .id(500L)
                .title("Leave Approved")
                .message("Your leave request has been approved.")
                .type("LEAVE_APPROVAL")
                .isRead(false)
                .build();

        when(notificationService.getUserNotifications(100L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/notifications").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(500))
                .andExpect(jsonPath("$.data[0].title").value("Leave Approved"));
    }

    @Test
    @DisplayName("PUT /api/v1/notifications/{id}/read should mark notification as read")
    void markAsRead_ReturnsOk() throws Exception {
        NotificationResponse response = NotificationResponse.builder()
                .id(500L)
                .title("Leave Approved")
                .isRead(true)
                .build();

        when(notificationService.markAsRead(500L, 100L)).thenReturn(response);

        mockMvc.perform(put("/api/v1/notifications/500/read").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(500))
                .andExpect(jsonPath("$.data.isRead").value(true));
    }
}
