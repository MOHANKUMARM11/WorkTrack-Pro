package com.worktrack.repository;

import com.worktrack.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {

    List<Announcement> findByCompanyIdOrderByCreatedAtDesc(Long companyId);

    List<Announcement> findByCompanyIdAndTargetRoleInOrderByCreatedAtDesc(Long companyId, List<String> targetRoles);
}
