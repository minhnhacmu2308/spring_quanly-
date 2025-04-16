package com.quan_ly.spring.services.impls;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.quan_ly.spring.exceptions.DocumentUploadException;
import com.quan_ly.spring.models.Document;
import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.repositories.DocumentRepository;
import com.quan_ly.spring.services.DocumentService;
import com.quan_ly.spring.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
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
    public Document saveDocument(MultipartFile file, String title, User user, Project project) {
        try {

//            String fileName = toSnakeCase(title);
//            String lastName = getExtension(file.getOriginalFilename());
            String fileName = file.getOriginalFilename();
            // Cho phép các định dạng file: PDF, DOCX, TXT, ZIP
            String contentType = file.getContentType();
            if (!"application/pdf".equals(contentType) &&
                    !"application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType) &&
                    !"text/plain".equals(contentType) &&
                    !"application/zip".equals(contentType)) {
                throw new DocumentUploadException("Only PDF, DOCX, TXT files are accepted.");
            }

            // Upload file lên Cloudinary với resource_type là "raw"
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of(
                    "resource_type", "raw",  // Giữ nguyên định dạng file
                    "public_id", "documents/" + fileName,
                    "use_filename", true,
                    "unique_filename", false
            ));

            String fileUrl = uploadResult.get("secure_url").toString();

            Document document = new Document();
            document.setTitle(title);
            document.setFilePath(fileUrl);
            document.setUploadedBy(user);
            document.setProject(project);
            document.setUploadedAt(LocalDateTime.now());

            return documentRepository.save(document);
        } catch (IOException e) {
            throw new DocumentUploadException("Upload failed");
        }
    }


    public static String toSnakeCase(String input) {
        // B1: Chuẩn hóa unicode
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // B2: Loại bỏ dấu
        String noAccents = normalized.replaceAll("\\p{M}", "");

        // B3: Thay khoảng trắng bằng dấu gạch dưới
        String snake = noAccents.replaceAll("\\s+", "_");

        // B4: Chuyển về chữ thường
        return snake.toLowerCase();
    }

    public static String getExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex != fileName.length() - 1) {
            return fileName.substring(lastDotIndex); // Lấy từ dấu chấm trở đi
        }
        return ""; // Không có đuôi
    }


    @Override
    public Document updateDocument(Long id, String title, MultipartFile file,Project project) {
        Document document = getDocumentById(id);
        document.setTitle(title);
        document.setProject(project);
        if (file != null && !file.isEmpty()) {
            try {
                String contentType = file.getContentType();
//                String fileName = toSnakeCase(title);
//                String lastName = getExtension(file.getOriginalFilename());
                String fileName = file.getOriginalFilename();
                // Kiểm tra loại file hợp lệ
                if (!"application/pdf".equals(contentType) &&
                        !"application/vnd.openxmlformats-officedocument.wordprocessingml.document".equals(contentType) &&
                        !"text/plain".equals(contentType) &&
                        !"application/zip".equals(contentType)) {
                    throw new DocumentUploadException("Only PDF, DOCX, TXT files are accepted.");
                }

                // Upload file lên Cloudinary
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), Map.of(
                        "resource_type", "raw", // Giữ nguyên file
                        "public_id", "documents/" + fileName,
                        "use_filename", true,
                        "unique_filename", false
                ));
                System.out.println(uploadResult);

                String fileUrl = uploadResult.get("secure_url").toString();
                document.setFilePath(fileUrl);
            } catch (IOException e) {
                throw new DocumentUploadException("Upload failed");
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

