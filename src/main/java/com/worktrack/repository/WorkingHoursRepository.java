package com.worktrack.repository;

import com.worktrack.entity.WorkingHours;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorkingHoursRepository extends JpaRepository<WorkingHours, Long> {

    List<WorkingHours> findByCompanyId(Long companyId);

    Optional<WorkingHours> findByCompanyIdAndDayOfWeek(Long companyId, String dayOfWeek);
}
