package com.worktrack.service;

import com.worktrack.dto.response.DocumentResponse;

import java.util.List;

public interface DocumentService {

    DocumentResponse getDocumentById(Long id);

    List<DocumentResponse> getDocumentsByCompanyId(Long companyId);

    void deleteDocument(Long id);
}
