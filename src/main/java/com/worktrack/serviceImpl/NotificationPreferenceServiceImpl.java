package com.worktrack.serviceImpl;

import com.worktrack.dto.request.NotificationPreferenceRequest;
import com.worktrack.dto.response.NotificationPreferenceResponse;
import com.worktrack.entity.NotificationPreference;
import com.worktrack.entity.User;
import com.worktrack.exception.custom.ResourceNotFoundException;
import com.worktrack.repository.NotificationPreferenceRepository;
import com.worktrack.repository.UserRepository;
import com.worktrack.service.NotificationPreferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationPreferenceServiceImpl
        implements NotificationPreferenceService {

    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @Override
    public List<NotificationPreferenceResponse> getUserPreferences(
            Long userId) {

        return preferenceRepository.findByUserId(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public NotificationPreferenceResponse updatePreference(
            Long userId,
            NotificationPreferenceRequest request) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("User not found"));

        String channel = request.getChannel()
                .trim()
                .toUpperCase();

        NotificationPreference preference =
                preferenceRepository
                        .findByUserIdAndChannel(userId, channel)
                        .orElseGet(() ->
                                NotificationPreference.builder()
                                        .user(user)
                                        .channel(channel)
                                        .enabled(true)
                                        .build());

        preference.setEnabled(request.getEnabled());

        NotificationPreference saved =
                preferenceRepository.save(preference);

        return toResponse(saved);
    }

    private NotificationPreferenceResponse toResponse(
            NotificationPreference preference) {

        return NotificationPreferenceResponse.builder()
                .id(preference.getId())
                .channel(preference.getChannel())
                .enabled(preference.getEnabled())
                .build();
    }
}