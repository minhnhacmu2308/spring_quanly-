package com.quan_ly.spring.services;

import com.quan_ly.spring.models.Document;
import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {
    List<Document> getAllDocuments();
    Document getDocumentById(Long id);
    Document saveDocument(MultipartFile file, String title, User user, Project project);
    Document updateDocument(Long id, String title, MultipartFile file,Project project);
    void deleteDocument(Long id);
}
