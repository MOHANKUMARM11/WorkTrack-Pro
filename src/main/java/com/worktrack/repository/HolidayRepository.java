package com.worktrack.repository;

import com.worktrack.entity.Holiday;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface HolidayRepository extends JpaRepository<Holiday, Long> {

    List<Holiday> findByCompanyId(Long companyId);

    List<Holiday> findByCompanyIdAndHolidayDateBetween(Long companyId, LocalDate startDate, LocalDate endDate);

    boolean existsByCompanyIdAndHolidayDate(Long companyId, LocalDate holidayDate);
}
