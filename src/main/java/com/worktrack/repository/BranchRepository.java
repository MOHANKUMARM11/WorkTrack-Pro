package com.worktrack.repository;

import com.worktrack.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    List<Branch> findByCompanyId(Long companyId);

    boolean existsByCompanyIdAndName(Long companyId, String name);
}
