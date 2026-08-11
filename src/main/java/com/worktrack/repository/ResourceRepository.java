package com.worktrack.repository;

import com.worktrack.entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResourceRepository extends JpaRepository<Resource, Long> {

    boolean existsByNameAndCompanyId(String name, Long companyId);

    Optional<Resource> findByNameAndCompanyId(String name, Long companyId);

    List<Resource> findByCompanyId(Long companyId);

    List<Resource> findByEmployeeId(Long employeeId);
}