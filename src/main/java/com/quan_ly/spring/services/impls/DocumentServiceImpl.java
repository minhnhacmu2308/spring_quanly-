package com.quan_ly.spring.services.impls;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.quan_ly.spring.models.Document;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.repositories.DocumentRepository;
import com.quan_ly.spring.services.DocumentService;
import com.quan_ly.spring.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    DocumentRepository documentRepository;
    @Autowired
    Cloudinary cloudinary;

    StringUtil stringUtil = new StringUtil();

    @Override
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }

    @Override
    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Document not found"));
    }

    @Override
    public Document saveDocument(MultipartFile file, String title, User user) {
        try {
            System.out.println("Received file: " + file.getOriginalFilename());
            System.out.println("Content type: " + file.getContentType());
            System.out.println("Size: " + file.getSize());

            // Chỉ cho phép file PDF và DOCX
            String contentType = file.getContentType();
            if (!"application/pdf".equals(contentType) &&
                    !"application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
                throw new IllegalArgumentException("Chỉ chấp nhận file PDF hoặc DOCX.");
            }

            // Upload file với resource_type là "raw"
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "resource_type", "raw",
                    "public_id", "documents/" + file.getOriginalFilename(),  // giữ tên gốc
                    "use_filename", true,
                    "unique_filename", false
            ));

            String fileUrl = uploadResult.get("secure_url").toString();

            Document document = new Document();
            document.setTitle(title);
            document.setFilePath(fileUrl);
            document.setUploadedBy(user);
            document.setUploadedAt(LocalDateTime.now());

            return documentRepository.save(document);
        } catch (IOException e) {
            throw new RuntimeException("Upload thất bại", e);
        }
    }


    @Override
    public Document updateDocument(Long id, String title, MultipartFile file) {
        Document document = getDocumentById(id);
        document.setTitle(title);

        if (file != null && !file.isEmpty()) {
            try {
                String contentType = file.getContentType();
                if (!"application/pdf".equals(contentType) &&
                        !"application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType)) {
                    throw new IllegalArgumentException("Chỉ chấp nhận file PDF hoặc DOCX.");
                }

                // Upload file với resource_type là "raw"
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of(
                        "resource_type", "raw",
                        "public_id", "documents/" + file.getOriginalFilename(),  // giữ tên gốc
                        "use_filename", true,
                        "unique_filename", false
                ));

                String fileUrl = uploadResult.get("secure_url").toString();
                document.setFilePath(fileUrl);
            } catch (IOException e) {
                throw new RuntimeException("Upload failed", e);
            }
        }

        return documentRepository.save(document);
    }

    @Override
    public void deleteDocument(Long id) {
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));
        String fileUrl = document.getFilePath(); // URL Cloudinary bạn lưu trong DB
        String publicId = stringUtil.extractPublicIdFromUrl(fileUrl);
        // Xóa file trên Cloudinary
        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (IOException e) {
            e.printStackTrace(); // hoặc log lỗi
        }

        // Xóa record trong DB
        documentRepository.deleteById(id);
    }
}

