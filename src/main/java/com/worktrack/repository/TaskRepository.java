package com.worktrack.repository;

import com.worktrack.constants.TaskStatus;
import com.worktrack.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByEmployeeId(Long employeeId);

    List<Task> findByCompanyId(Long companyId);

    List<Task> findByStatus(TaskStatus status);

    boolean existsByTitle(String title);

    long count();

    long countByStatus(TaskStatus status);
}