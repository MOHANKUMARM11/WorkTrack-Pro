package com.worktrack.service;

import com.worktrack.dto.request.AnnouncementRequest;
import com.worktrack.dto.response.AnnouncementResponse;

import java.util.List;

public interface AnnouncementService {

    AnnouncementResponse createAnnouncement(AnnouncementRequest request);

    AnnouncementResponse getAnnouncementById(Long id);

    List<AnnouncementResponse> getAnnouncementsByCompanyId(Long companyId);

    List<AnnouncementResponse> getAnnouncementsByCompanyAndRole(Long companyId, String userRole);

    void deleteAnnouncement(Long id);
}
