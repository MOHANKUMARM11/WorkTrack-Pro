package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.worktrack.dto.request.AnnouncementRequest;
import com.worktrack.dto.response.AnnouncementResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.AnnouncementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AnnouncementControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private AnnouncementService announcementService;

    @InjectMocks
    private AnnouncementController announcementController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(announcementController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/announcements should create announcement and return 201 CREATED")
    void createAnnouncement_ReturnsCreated() throws Exception {
        AnnouncementRequest request = AnnouncementRequest.builder()
                .title("Office Townhall")
                .content("Townhall meeting on Friday at 4 PM")
                .companyId(1L)
                .createdByUserId(10L)
                .targetRole("ALL")
                .build();

        AnnouncementResponse response = AnnouncementResponse.builder()
                .id(100L)
                .title("Office Townhall")
                .content("Townhall meeting on Friday at 4 PM")
                .companyId(1L)
                .build();

        when(announcementService.createAnnouncement(any(AnnouncementRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/announcements")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.title").value("Office Townhall"));
    }

    @Test
    @DisplayName("GET /api/v1/announcements/company/{companyId} should return list of announcements")
    void getAnnouncementsByCompany_ReturnsList() throws Exception {
        AnnouncementResponse response = AnnouncementResponse.builder()
                .id(100L)
                .title("Office Townhall")
                .companyId(1L)
                .build();

        when(announcementService.getAnnouncementsByCompanyId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/announcements/company/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].title").value("Office Townhall"));
    }
}
