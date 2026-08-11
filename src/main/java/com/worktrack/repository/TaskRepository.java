package com.worktrack.repository;

import com.worktrack.constants.TaskStatus;
import com.worktrack.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findByEmployeeId(Long employeeId);

    List<Task> findByCompanyId(Long companyId);

    List<Task> findByStatus(TaskStatus status);

    boolean existsByTitle(String title);

    long count();

    long countByStatus(TaskStatus status);

    Optional<Task> findByTitle(String title);

    long countByCompanyId(Long companyId);

    long countByCompanyIdAndStatus(
            Long companyId,
            TaskStatus status);

    @Query("""
        SELECT COUNT(t)
        FROM Task t
        WHERE t.company.id = :companyId
        AND t.dueDate < CURRENT_DATE
        AND t.status <> com.worktrack.constants.TaskStatus.COMPLETED
        """)
    long countOverdueTasksByCompanyId(
            @Param("companyId") Long companyId);
}