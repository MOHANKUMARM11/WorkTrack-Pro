package com.worktrack.repository;

import com.worktrack.entity.TaskAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    List<TaskAssignment> findByTaskId(Long taskId);

    List<TaskAssignment> findByEmployeeId(Long employeeId);

    boolean existsByTaskIdAndEmployeeId(Long taskId, Long employeeId);

    Optional<TaskAssignment> findByTaskIdAndEmployeeId(Long taskId, Long employeeId);

    void deleteByTaskIdAndEmployeeId(Long taskId, Long employeeId);
}
