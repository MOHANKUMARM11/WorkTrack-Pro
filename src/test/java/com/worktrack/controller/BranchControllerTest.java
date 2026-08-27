package com.worktrack.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.worktrack.dto.request.BranchRequest;
import com.worktrack.dto.response.BranchResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.BranchService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BranchControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private BranchService branchService;

    @InjectMocks
    private BranchController branchController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(branchController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/branches should create branch and return 201 CREATED")
    void createBranch_ReturnsCreated() throws Exception {
        BranchRequest request = BranchRequest.builder()
                .name("Downtown Branch")
                .address("123 Main St")
                .city("Metropolis")
                .country("USA")
                .companyId(1L)
                .build();

        BranchResponse response = BranchResponse.builder()
                .id(10L)
                .name("Downtown Branch")
                .companyId(1L)
                .build();

        when(branchService.createBranch(any(BranchRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/branches")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Downtown Branch"));
    }

    @Test
    @DisplayName("GET /api/v1/branches/{id} should return 200 OK")
    void getBranchById_ReturnsOk() throws Exception {
        BranchResponse response = BranchResponse.builder()
                .id(10L)
                .name("Downtown Branch")
                .companyId(1L)
                .build();

        when(branchService.getBranchById(10L)).thenReturn(response);

        mockMvc.perform(get("/api/v1/branches/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("Downtown Branch"));
    }

    @Test
    @DisplayName("GET /api/v1/branches/company/{companyId} should return list of branches")
    void getBranchesByCompanyId_ReturnsList() throws Exception {
        BranchResponse response = BranchResponse.builder()
                .id(10L)
                .name("Downtown Branch")
                .companyId(1L)
                .build();

        when(branchService.getBranchesByCompanyId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/branches/company/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].name").value("Downtown Branch"));
    }

    @Test
    @DisplayName("DELETE /api/v1/branches/{id} should return 24 NO CONTENT")
    void deleteBranch_ReturnsNoContent() throws Exception {
        doNothing().when(branchService).deleteBranch(10L);

        mockMvc.perform(delete("/api/v1/branches/10"))
                .andExpect(status().isNoContent());
    }
}
