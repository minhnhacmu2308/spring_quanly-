package com.quan_ly.spring.controllers;

import com.cloudinary.Cloudinary;
import com.quan_ly.spring.constants.CommonConstant;
import com.quan_ly.spring.exceptions.DocumentUploadException;
import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.Process;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.ProjectService;
import com.quan_ly.spring.services.ProcessService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/process")
public class ProcessController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ProcessService processService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    Cloudinary cloudinary;

    @GetMapping("/home")
    public String listProcess(Model model) {
        model.addAttribute("processs", processService.getAllProcess());
        model.addAttribute("projects", projectService.getAllProjects());
        return "public/process";
    }

    @PostMapping("/create")
    public String createProcess(@ModelAttribute("process") Process process,
                             RedirectAttributes redirectAttributes,
                             HttpSession session, HttpServletRequest request,@RequestParam("file") MultipartFile file) {
        // Xử lý file
        try {
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
            User user = (User) session.getAttribute("user");
            process.setFilePath(fileUrl);
            process.setReportedBy(user);
            process.setReportDate(LocalDateTime.now());
            Long projectId = Long.parseLong(request.getParameter("projectId"));
            process.setProject(projectService.getProjectById(projectId).get());
            processService.createProcess(process);
            redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                    messageSource.getMessage("create_success", null, Locale.getDefault()));
            return "redirect:/process/home";
        } catch (IOException e) {
            throw new DocumentUploadException("Upload failed");
        }
        //end
    }
}
