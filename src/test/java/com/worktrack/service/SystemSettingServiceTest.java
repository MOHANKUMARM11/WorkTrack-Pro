package com.worktrack.service;

import com.worktrack.dto.request.SystemSettingRequest;
import com.worktrack.dto.response.SystemSettingResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.SystemSetting;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.SystemSettingRepository;
import com.worktrack.serviceImpl.SystemSettingServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SystemSettingServiceTest {

    @Mock
    private SystemSettingRepository systemSettingRepository;

    @Mock
    private CompanyRepository companyRepository;

    @InjectMocks
    private SystemSettingServiceImpl systemSettingService;

    private Company sampleCompany;
    private SystemSetting sampleSetting;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Tech").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleSetting = SystemSetting.builder()
                .key("GEOFENCE_RADIUS_METERS")
                .value("200")
                .category("ATTENDANCE")
                .description("Maximum radius for checkin")
                .company(sampleCompany)
                .build();
        ReflectionTestUtils.setField(sampleSetting, "id", 50L);
    }

    @Test
    @DisplayName("Should save system setting successfully")
    void saveOrUpdateSetting_Success() {
        SystemSettingRequest request = SystemSettingRequest.builder()
                .key("GEOFENCE_RADIUS_METERS")
                .value("200")
                .category("ATTENDANCE")
                .companyId(1L)
                .build();

        when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
        when(systemSettingRepository.findByCompanyIdAndKey(1L, "GEOFENCE_RADIUS_METERS")).thenReturn(Optional.empty());
        when(systemSettingRepository.save(any(SystemSetting.class))).thenReturn(sampleSetting);

        SystemSettingResponse response = systemSettingService.saveOrUpdateSetting(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(50L);
        assertThat(response.getKey()).isEqualTo("GEOFENCE_RADIUS_METERS");
    }

    @Test
    @DisplayName("Should return setting by key and company ID")
    void getSettingByKeyAndCompany_Success() {
        when(systemSettingRepository.findByCompanyIdAndKey(1L, "GEOFENCE_RADIUS_METERS"))
                .thenReturn(Optional.of(sampleSetting));

        SystemSettingResponse response = systemSettingService.getSettingByKeyAndCompany("GEOFENCE_RADIUS_METERS", 1L);

        assertThat(response).isNotNull();
        assertThat(response.getValue()).isEqualTo("200");
    }

    @Test
    @DisplayName("Should return settings list by company ID")
    void getSettingsByCompanyId_Success() {
        when(companyRepository.existsById(1L)).thenReturn(true);
        when(systemSettingRepository.findByCompanyId(1L)).thenReturn(List.of(sampleSetting));

        List<SystemSettingResponse> responses = systemSettingService.getSettingsByCompanyId(1L);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getKey()).isEqualTo("GEOFENCE_RADIUS_METERS");
    }
}
