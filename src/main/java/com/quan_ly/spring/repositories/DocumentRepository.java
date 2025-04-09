package com.quan_ly.spring.repositories;

import com.quan_ly.spring.models.Document;
import com.quan_ly.spring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    // Bạn có thể thêm các query tùy chỉnh nếu cần sau này
    List<Document> findByUploadedBy(User user);
}