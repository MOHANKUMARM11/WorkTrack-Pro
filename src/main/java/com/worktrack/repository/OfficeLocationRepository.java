package com.worktrack.repository;

import com.worktrack.entity.OfficeLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfficeLocationRepository
        extends JpaRepository<OfficeLocation, Long> {

    List<OfficeLocation> findByCompanyId(Long companyId);
}