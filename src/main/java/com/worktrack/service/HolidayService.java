package com.worktrack.service;

import com.worktrack.dto.request.HolidayRequest;
import com.worktrack.dto.response.HolidayResponse;

import java.time.LocalDate;
import java.util.List;

public interface HolidayService {

    HolidayResponse createHoliday(HolidayRequest request);

    HolidayResponse getHolidayById(Long id);

    List<HolidayResponse> getHolidaysByCompanyId(Long companyId);

    List<HolidayResponse> getHolidaysBetweenDates(Long companyId, LocalDate startDate, LocalDate endDate);

    HolidayResponse updateHoliday(Long id, HolidayRequest request);

    void deleteHoliday(Long id);
}
