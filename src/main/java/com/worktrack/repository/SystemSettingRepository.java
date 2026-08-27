package com.worktrack.repository;

import com.worktrack.entity.SystemSetting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SystemSettingRepository extends JpaRepository<SystemSetting, Long> {

    List<SystemSetting> findByCompanyId(Long companyId);

    Optional<SystemSetting> findByCompanyIdAndKey(Long companyId, String key);

    Optional<SystemSetting> findByKeyAndCompanyIdIsNull(String key);

    boolean existsByCompanyIdAndKey(Long companyId, String key);
}
