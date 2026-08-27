package com.worktrack.service;

import com.worktrack.dto.request.SystemSettingRequest;
import com.worktrack.dto.response.SystemSettingResponse;

import java.util.List;

public interface SystemSettingService {

    SystemSettingResponse saveOrUpdateSetting(SystemSettingRequest request);

    SystemSettingResponse getSettingByKeyAndCompany(String key, Long companyId);

    List<SystemSettingResponse> getSettingsByCompanyId(Long companyId);

    List<SystemSettingResponse> getAllSettings();

    void deleteSetting(Long id);
}
