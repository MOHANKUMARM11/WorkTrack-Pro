package com.worktrack.repository;

import com.worktrack.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByCompanyId(Long companyId);

    List<Document> findByUploaderId(Long uploaderId);
}
