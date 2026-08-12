package com.worktrack.service;

import com.worktrack.dto.request.NotificationPreferenceRequest;
import com.worktrack.dto.response.NotificationPreferenceResponse;

import java.util.List;

public interface NotificationPreferenceService {

    List<NotificationPreferenceResponse> getUserPreferences(
            Long userId);

    NotificationPreferenceResponse updatePreference(
            Long userId,
            NotificationPreferenceRequest request);
}