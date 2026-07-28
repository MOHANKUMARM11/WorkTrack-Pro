package com.worktrack.repository;

import com.worktrack.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByName(String name);

    Optional<Company> findByRegistrationNumber(String registrationNumber);

    boolean existsByName(String name);

    boolean existsByRegistrationNumber(String registrationNumber);

    long count();

}