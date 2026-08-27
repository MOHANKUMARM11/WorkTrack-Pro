package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.worktrack.constants.TaskPriority;
import com.worktrack.constants.TaskStatus;
import com.worktrack.dto.request.TaskRequest;
import com.worktrack.dto.response.TaskResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(taskController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/tasks should create task and return 201 CREATED")
    void createTask_ReturnsCreated() throws Exception {
        TaskRequest request = new TaskRequest();
        request.setTitle("Database Migration");
        request.setDescription("Run V20 migration");
        request.setPriority(TaskPriority.HIGH);
        request.setDueDate(LocalDate.now().plusDays(5));
        request.setEmployeeId(100L);
        request.setCompanyId(1L);

        TaskResponse response = TaskResponse.builder()
                .id(10L)
                .title("Database Migration")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .build();

        when(taskService.createTask(any(TaskRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Database Migration"));
    }

    @Test
    @DisplayName("POST /api/v1/tasks/{id}/assign should assign employees and return 200 OK")
    void assignEmployeesToTask_ReturnsOk() throws Exception {
        TaskResponse response = TaskResponse.builder().id(10L).title("Database Migration").build();

        when(taskService.assignEmployeesToTask(eq(10L), eq(List.of(101L, 102L)))).thenReturn(response);

        mockMvc.perform(post("/api/v1/tasks/10/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(List.of(101L, 102L))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }
}
