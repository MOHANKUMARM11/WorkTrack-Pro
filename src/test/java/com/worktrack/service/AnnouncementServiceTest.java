package com.worktrack.service;

import com.worktrack.dto.request.AnnouncementRequest;
import com.worktrack.dto.response.AnnouncementResponse;
import com.worktrack.entity.Announcement;
import com.worktrack.entity.Company;
import com.worktrack.entity.User;
import com.worktrack.repository.AnnouncementRepository;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.UserRepository;
import com.worktrack.serviceImpl.AnnouncementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnnouncementServiceTest {

    @Mock
    private AnnouncementRepository announcementRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @Mock
    private FcmPushNotificationService fcmPushNotificationService;

    @InjectMocks
    private AnnouncementServiceImpl announcementService;

    private Company sampleCompany;
    private User sampleUser;
    private Announcement sampleAnnouncement;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Tech").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleUser = User.builder().email("admin@acme.com").build();
        ReflectionTestUtils.setField(sampleUser, "id", 10L);

        sampleAnnouncement = Announcement.builder()
                .title("Office Townhall")
                .content("Townhall meeting on Friday at 4 PM")
                .company(sampleCompany)
                .createdBy(sampleUser)
                .targetRole("ALL")
                .build();
        ReflectionTestUtils.setField(sampleAnnouncement, "id", 100L);
    }

    @Test
    @DisplayName("Should successfully create announcement and trigger websocket broadcast & FCM notification")
    void createAnnouncement_Success() {
        AnnouncementRequest request = AnnouncementRequest.builder()
                .title("Office Townhall")
                .content("Townhall meeting on Friday at 4 PM")
                .companyId(1L)
                .createdByUserId(10L)
                .targetRole("ALL")
                .build();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
        when(userRepository.findById(10L)).thenReturn(Optional.of(sampleUser));
        when(announcementRepository.save(any(Announcement.class))).thenReturn(sampleAnnouncement);

        AnnouncementResponse response = announcementService.createAnnouncement(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getTitle()).isEqualTo("Office Townhall");

        verify(fcmPushNotificationService).sendTopicNotification(eq("company_1"), eq("Office Townhall"), any());
    }

    @Test
    @DisplayName("Should return announcements by company ID")
    void getAnnouncementsByCompanyId_Success() {
        when(companyRepository.existsById(1L)).thenReturn(true);
        when(announcementRepository.findByCompanyIdOrderByCreatedAtDesc(1L)).thenReturn(List.of(sampleAnnouncement));

        List<AnnouncementResponse> responses = announcementService.getAnnouncementsByCompanyId(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getTitle()).isEqualTo("Office Townhall");
    }
}
