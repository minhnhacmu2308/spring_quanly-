package com.quan_ly.spring.controllers;

import com.cloudinary.Cloudinary;
import com.quan_ly.spring.constants.CommonConstant;
import com.quan_ly.spring.exceptions.DocumentUploadException;
import com.quan_ly.spring.models.Expense;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.ExpenseService;
import com.quan_ly.spring.services.ProjectService;
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

@Controller
@RequestMapping("/expense")
public class ExpenseController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    Cloudinary cloudinary;

    @GetMapping("/home")
    public String listProcess(Model model) {
        model.addAttribute("expenses", expenseService.getAllExpense());
        model.addAttribute("projects", projectService.getAllProjects());
        return "public/expense";
    }

    @PostMapping("/create")
    public String createProcess(@ModelAttribute("expense") Expense expense,
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
            expense.setFilePath(fileUrl);
            expense.setReportedBy(user);
            expense.setReportDate(LocalDateTime.now());
            Long projectId = Long.parseLong(request.getParameter("projectId"));
            expense.setProject(projectService.getProjectById(projectId).get());
            expenseService.createExpense(expense);
            redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                    messageSource.getMessage("create_success", null, Locale.getDefault()));
            return "redirect:/expense/home";
        } catch (IOException e) {
            throw new DocumentUploadException("Upload failed");
        }
        //end
    }
}
