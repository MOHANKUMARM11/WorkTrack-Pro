package com.worktrack.serviceImpl;

import com.worktrack.dto.request.HolidayRequest;
import com.worktrack.dto.response.HolidayResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Holiday;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.HolidayRepository;
import com.worktrack.service.HolidayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class HolidayServiceImpl implements HolidayService {

    private final HolidayRepository holidayRepository;
    private final CompanyRepository companyRepository;

    @Override
    public HolidayResponse createHoliday(HolidayRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new CompanyNotFoundException("Company not found with id: " + request.getCompanyId()));

        if (holidayRepository.existsByCompanyIdAndHolidayDate(request.getCompanyId(), request.getHolidayDate())) {
            throw new IllegalArgumentException("Holiday already exists on date: " + request.getHolidayDate());
        }

        Holiday holiday = Holiday.builder()
                .name(request.getName())
                .holidayDate(request.getHolidayDate())
                .description(request.getDescription())
                .optional(Boolean.TRUE.equals(request.getOptional()))
                .company(company)
                .build();

        Holiday saved = holidayRepository.save(holiday);
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public HolidayResponse getHolidayById(Long id) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Holiday not found with id: " + id));
        return mapToResponse(holiday);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HolidayResponse> getHolidaysByCompanyId(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found with id: " + companyId);
        }
        return holidayRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HolidayResponse> getHolidaysBetweenDates(Long companyId, LocalDate startDate, LocalDate endDate) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found with id: " + companyId);
        }
        return holidayRepository.findByCompanyIdAndHolidayDateBetween(companyId, startDate, endDate).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public HolidayResponse updateHoliday(Long id, HolidayRequest request) {
        Holiday holiday = holidayRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Holiday not found with id: " + id));

        if (!holiday.getHolidayDate().equals(request.getHolidayDate()) &&
                holidayRepository.existsByCompanyIdAndHolidayDate(holiday.getCompany().getId(), request.getHolidayDate())) {
            throw new IllegalArgumentException("Holiday already exists on date: " + request.getHolidayDate());
        }

        holiday.setName(request.getName());
        holiday.setHolidayDate(request.getHolidayDate());
        holiday.setDescription(request.getDescription());
        holiday.setOptional(Boolean.TRUE.equals(request.getOptional()));

        Holiday updated = holidayRepository.save(holiday);
        return mapToResponse(updated);
    }

    @Override
    public void deleteHoliday(Long id) {
        if (!holidayRepository.existsById(id)) {
            throw new IllegalArgumentException("Holiday not found with id: " + id);
        }
        holidayRepository.deleteById(id);
    }

    private HolidayResponse mapToResponse(Holiday holiday) {
        return HolidayResponse.builder()
                .id(holiday.getId())
                .name(holiday.getName())
                .holidayDate(holiday.getHolidayDate())
                .description(holiday.getDescription())
                .optional(holiday.getOptional())
                .companyId(holiday.getCompany().getId())
                .companyName(holiday.getCompany().getName())
                .createdAt(holiday.getCreatedAt())
                .updatedAt(holiday.getUpdatedAt())
                .build();
    }
}
