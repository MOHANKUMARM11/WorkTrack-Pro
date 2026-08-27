package com.worktrack.service;

import com.worktrack.constants.TaskPriority;
import com.worktrack.constants.TaskStatus;
import com.worktrack.dto.request.TaskRequest;
import com.worktrack.dto.response.TaskResponse;
import com.worktrack.entity.Company;
import com.worktrack.entity.Employee;
import com.worktrack.entity.Task;
import com.worktrack.entity.TaskAssignment;
import com.worktrack.exception.custom.TaskTitleAlreadyExistsException;
import com.worktrack.notification.NotificationEventProducer;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.EmployeeRepository;
import com.worktrack.repository.TaskAssignmentRepository;
import com.worktrack.repository.TaskRepository;
import com.worktrack.serviceImpl.TaskServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private TaskAssignmentRepository taskAssignmentRepository;

    @Mock
    private NotificationEventProducer notificationEventProducer;

    @InjectMocks
    private TaskServiceImpl taskService;

    private Company sampleCompany;
    private Employee sampleEmployee;
    private Task sampleTask;

    @BeforeEach
    void setUp() {
        sampleCompany = Company.builder().name("Acme Tech").build();
        ReflectionTestUtils.setField(sampleCompany, "id", 1L);

        sampleEmployee = Employee.builder().firstName("John").lastName("Doe").company(sampleCompany).build();
        ReflectionTestUtils.setField(sampleEmployee, "id", 100L);

        sampleTask = Task.builder()
                .title("Database Migration")
                .description("Run V20 migration")
                .priority(TaskPriority.HIGH)
                .status(TaskStatus.TODO)
                .dueDate(LocalDate.now().plusDays(5))
                .employee(sampleEmployee)
                .company(sampleCompany)
                .assignments(new ArrayList<>())
                .build();
        ReflectionTestUtils.setField(sampleTask, "id", 10L);
    }

    private TaskRequest createTaskRequest(String title) {
        TaskRequest request = new TaskRequest();
        request.setTitle(title);
        request.setDescription("Run V20 migration");
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDate.now().plusDays(5));
        request.setEmployeeId(100L);
        request.setCompanyId(1L);
        return request;
    }

    @Nested
    @DisplayName("Create Task Tests")
    class CreateTaskTests {

        @Test
        @DisplayName("Should create task successfully and assign initial employee")
        void createTask_Success() {
            TaskRequest request = createTaskRequest("Database Migration");

            when(taskRepository.existsByTitle("Database Migration")).thenReturn(false);
            when(employeeRepository.findById(100L)).thenReturn(Optional.of(sampleEmployee));
            when(companyRepository.findById(1L)).thenReturn(Optional.of(sampleCompany));
            when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

            TaskResponse response = taskService.createTask(request);

            assertThat(response).isNotNull();
            assertThat(response.getId()).isEqualTo(10L);
            assertThat(response.getTitle()).isEqualTo("Database Migration");

            verify(taskAssignmentRepository).save(any(TaskAssignment.class));
            verify(notificationEventProducer).publishTaskAssigned(any());
        }

        @Test
        @DisplayName("Should throw TaskTitleAlreadyExistsException when title duplicated")
        void createTask_DuplicateTitle() {
            TaskRequest request = createTaskRequest("Database Migration");
            when(taskRepository.existsByTitle("Database Migration")).thenReturn(true);

            assertThatThrownBy(() -> taskService.createTask(request))
                    .isInstanceOf(TaskTitleAlreadyExistsException.class);
        }
    }

    @Nested
    @DisplayName("Multi-Employee Assignment Tests")
    class MultiAssignmentTests {

        @Test
        @DisplayName("Should assign multiple employees to task")
        void assignEmployeesToTask_Success() {
            Employee secondEmployee = Employee.builder().firstName("Jane").lastName("Smith").build();
            ReflectionTestUtils.setField(secondEmployee, "id", 101L);

            when(taskRepository.findById(10L)).thenReturn(Optional.of(sampleTask));
            when(taskAssignmentRepository.existsByTaskIdAndEmployeeId(10L, 101L)).thenReturn(false);
            when(employeeRepository.findById(101L)).thenReturn(Optional.of(secondEmployee));

            TaskResponse response = taskService.assignEmployeesToTask(10L, List.of(101L));

            assertThat(response).isNotNull();
            verify(taskAssignmentRepository).save(any(TaskAssignment.class));
            verify(notificationEventProducer).publishTaskAssigned(any());
        }

        @Test
        @DisplayName("Should unassign employee from task")
        void unassignEmployeeFromTask_Success() {
            TaskAssignment assignment = TaskAssignment.builder().id(1L).task(sampleTask).employee(sampleEmployee).build();
            sampleTask.getAssignments().add(assignment);

            when(taskRepository.findById(10L)).thenReturn(Optional.of(sampleTask));
            when(taskAssignmentRepository.findByTaskIdAndEmployeeId(10L, 100L)).thenReturn(Optional.of(assignment));

            TaskResponse response = taskService.unassignEmployeeFromTask(10L, 100L);

            assertThat(response).isNotNull();
            verify(taskAssignmentRepository).delete(assignment);
        }
    }
}
