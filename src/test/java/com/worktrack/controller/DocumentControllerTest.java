package com.worktrack.controller;

import com.worktrack.dto.response.DocumentResponse;
import com.worktrack.exception.handler.GlobalExceptionHandler;
import com.worktrack.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class DocumentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DocumentService documentService;

    @InjectMocks
    private DocumentController documentController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(documentController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("GET /api/v1/documents/company/{companyId} should return company documents")
    void getDocumentsByCompany_ReturnsList() throws Exception {
        DocumentResponse response = DocumentResponse.builder()
                .id(1L)
                .fileName("policy.pdf")
                .companyId(1L)
                .build();

        when(documentService.getDocumentsByCompanyId(1L)).thenReturn(List.of(response));

        mockMvc.perform(get("/api/v1/documents/company/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].fileName").value("policy.pdf"));
    }
}
