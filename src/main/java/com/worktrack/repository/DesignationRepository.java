package com.worktrack.repository;

import com.worktrack.entity.Designation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DesignationRepository extends JpaRepository<Designation, Long> {

    List<Designation> findByCompanyId(Long companyId);

    boolean existsByCompanyIdAndTitle(Long companyId, String title);
}
