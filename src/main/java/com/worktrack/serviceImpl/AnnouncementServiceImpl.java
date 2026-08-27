package com.worktrack.serviceImpl;

import com.worktrack.dto.request.AnnouncementRequest;
import com.worktrack.dto.response.AnnouncementResponse;
import com.worktrack.entity.Announcement;
import com.worktrack.entity.Company;
import com.worktrack.entity.User;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.AnnouncementRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.UserRepository;
import com.worktrack.service.AnnouncementService;
import com.worktrack.service.FcmPushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class AnnouncementServiceImpl implements AnnouncementService {

    private final AnnouncementRepository announcementRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final FcmPushNotificationService fcmPushNotificationService;

    @Override
    public AnnouncementResponse createAnnouncement(AnnouncementRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found"));

        User createdBy = null;
        if (request.getCreatedByUserId() != null) {
            createdBy = userRepository.findById(request.getCreatedByUserId()).orElse(null);
        }

        Announcement announcement = Announcement.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .company(company)
                .createdBy(createdBy)
                .targetRole(request.getTargetRole() != null ? request.getTargetRole() : "ALL")
                .build();

        Announcement saved = announcementRepository.save(announcement);
        AnnouncementResponse response = mapToResponse(saved);

        // Broadcast live via STOMP WebSocket
        try {
            messagingTemplate.convertAndSend("/topic/announcements", response);
        } catch (Exception ignored) {
            // Silently handle if broker is detached in unit test context
        }

        // Send FCM topic push notification
        fcmPushNotificationService.sendTopicNotification("company_" + company.getId(), saved.getTitle(), saved.getContent());

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public AnnouncementResponse getAnnouncementById(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with id: " + id));
        return mapToResponse(announcement);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAnnouncementsByCompanyId(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }
        return announcementRepository.findByCompanyIdOrderByCreatedAtDesc(companyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnnouncementResponse> getAnnouncementsByCompanyAndRole(Long companyId, String userRole) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }
        List<String> targetRoles = List.of("ALL", userRole.toUpperCase());
        return announcementRepository.findByCompanyIdAndTargetRoleInOrderByCreatedAtDesc(companyId, targetRoles).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteAnnouncement(Long id) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Announcement not found with id: " + id));
        announcementRepository.delete(announcement);
    }

    private AnnouncementResponse mapToResponse(Announcement announcement) {
        return AnnouncementResponse.builder()
                .id(announcement.getId())
                .title(announcement.getTitle())
                .content(announcement.getContent())
                .companyId(announcement.getCompany().getId())
                .companyName(announcement.getCompany().getName())
                .createdByUserId(announcement.getCreatedBy() != null ? announcement.getCreatedBy().getId() : null)
                .createdByUserName(announcement.getCreatedBy() != null ? announcement.getCreatedBy().getEmail() : "System")
                .targetRole(announcement.getTargetRole())
                .createdAt(announcement.getCreatedAt())
                .updatedAt(announcement.getUpdatedAt())
                .build();
    }
}
