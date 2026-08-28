package com.worktrack.serviceImpl;

import com.worktrack.dto.response.DocumentResponse;
import com.worktrack.entity.Document;
import com.worktrack.exception.custom.CompanyNotFoundException;
import com.worktrack.exception.custom.ResourceNotFoundException;
import com.worktrack.repository.CompanyRepository;
import com.worktrack.repository.DocumentRepository;
import com.worktrack.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final CompanyRepository companyRepository;

    @Override
    @Transactional(readOnly = true)
    public DocumentResponse getDocumentById(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        return mapToResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponse> getDocumentsByCompanyId(Long companyId) {
        if (!companyRepository.existsById(companyId)) {
            throw new CompanyNotFoundException("Company not found");
        }
        return documentRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + id));
        documentRepository.delete(document);
    }

    private DocumentResponse mapToResponse(Document doc) {
        return DocumentResponse.builder()
                .id(doc.getId())
                .fileName(doc.getFileName())
                .fileType(doc.getFileType())
                .fileSize(doc.getFileSize())
                .filePath(doc.getFilePath())
                .category(doc.getCategory())
                .uploaderId(doc.getUploader() != null ? doc.getUploader().getId() : null)
                .companyId(doc.getCompany().getId())
                .createdAt(doc.getCreatedAt())
                .build();
    }
}
