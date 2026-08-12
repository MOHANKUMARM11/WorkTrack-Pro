package com.worktrack.controller;

import com.worktrack.common.response.ApiResponse;
import com.worktrack.dto.request.NotificationPreferenceRequest;
import com.worktrack.dto.response.NotificationPreferenceResponse;
import com.worktrack.entity.User;
import com.worktrack.service.NotificationPreferenceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notifications/preferences")
@RequiredArgsConstructor
public class NotificationPreferenceController {

    private final NotificationPreferenceService preferenceService;

    @GetMapping
    public ApiResponse<List<NotificationPreferenceResponse>> getPreferences(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        List<NotificationPreferenceResponse> preferences =
                preferenceService.getUserPreferences(user.getId());

        return ApiResponse.<List<NotificationPreferenceResponse>>builder()
                .success(true)
                .message("Notification preferences retrieved successfully")
                .data(preferences)
                .build();
    }

    @PutMapping
    public ApiResponse<NotificationPreferenceResponse> updatePreference(
            @Valid @RequestBody NotificationPreferenceRequest request,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();

        NotificationPreferenceResponse preference =
                preferenceService.updatePreference(
                        user.getId(),
                        request);

        return ApiResponse.<NotificationPreferenceResponse>builder()
                .success(true)
                .message("Notification preference updated successfully")
                .data(preference)
                .build();
    }
}