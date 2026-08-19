package com.worktrack.repository;

import com.worktrack.entity.Geofence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GeofenceRepository
        extends JpaRepository<Geofence, Long> {

    Optional<Geofence> findByOfficeLocationId(
            Long officeLocationId);
}