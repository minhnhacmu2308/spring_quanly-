package com.quan_ly.spring.controllers;

import com.cloudinary.Cloudinary;
import com.quan_ly.spring.constants.CommonConstant;
import com.quan_ly.spring.enums.Role;
import com.quan_ly.spring.exceptions.DocumentUploadException;
import com.quan_ly.spring.models.Document;
import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.Risk;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.NotificationService;
import com.quan_ly.spring.services.ProjectService;
import com.quan_ly.spring.services.RiskService;
import com.quan_ly.spring.utils.SendNotificationUtil;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/risk")
public class RiskController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private RiskService riskService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    SendNotificationUtil sendNotificationUtil;

    @Autowired
    NotificationService notificationService;

    @Autowired
    Cloudinary cloudinary;

    @GetMapping("/home")
    public String listRisks(Model model,  HttpSession session) {
        User user = (User) session.getAttribute("user");
        List<Risk> risks = user.getRole() == Role.MANAGER ? riskService.getRisksByManagerId(user.getUserId()) : riskService.getRisksReportedByUser(user);
        model.addAttribute("risks", risks);
        model.addAttribute("projects", projectService.getProjectByUserAndDate(user, LocalDate.now()));
        return "public/risk"; // View hiển thị danh sách rủi ro
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("risk", new Risk());
        return "risk/form"; // Form tạo rủi ro
    }

    @PostMapping("/create")
    public String createRisk(@ModelAttribute("risk") Risk risk,
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
            risk.setFilePath(fileUrl);
            risk.setReportedBy(user);
            risk.setReportedAt(LocalDateTime.now());
            risk.setUpdatedAt(LocalDateTime.now());
            Long projectId = Long.parseLong(request.getParameter("projectId"));
            Project pj = projectService.getProjectById(projectId).get();
            risk.setProject(pj);
            riskService.createRisk(risk);

            //notification
            sendNotificationUtil.sendRiskNotificationToManager(risk);

            redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                    messageSource.getMessage("create_success", null, Locale.getDefault()));
            return "redirect:/risk/home";
        } catch (IOException e) {
            throw new DocumentUploadException("Upload failed");
        }
        //end
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return riskService.getRiskById(id).map(risk -> {
            model.addAttribute("risk", risk);
            return "risk/form"; // Form chỉnh sửa rủi ro
        }).orElse("redirect:/risk/home");
    }

    @PostMapping("/edit/{id}")
    public String updateRisk(@PathVariable Long id,
                             @ModelAttribute("risk") Risk updatedRisk,
                             @RequestParam("projectId") String prId,
                             @RequestParam(value = "file", required = false) MultipartFile file,
                             RedirectAttributes redirectAttributes, HttpSession session) {

        Optional<Risk> optionalRisk = riskService.getRiskById(id);
        if (optionalRisk.isEmpty()) {
            redirectAttributes.addFlashAttribute(CommonConstant.ERROR_MESSAGE,
                    messageSource.getMessage("risk_not_found", null, Locale.getDefault()));
            return "redirect:/risk/home";
        }
        User user = (User) session.getAttribute("user");
        Long projectId = Long.parseLong(prId);
        Project project = projectService.getProjectById(projectId).get();
        String filePath = optionalRisk.get().getFilePath();
        //xu ly file
        if (file != null && !file.isEmpty()) {
            try {
                String contentType = file.getContentType();
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

                filePath = uploadResult.get("secure_url").toString();

            } catch (IOException e) {
                throw new DocumentUploadException("Upload failed");
            }
        }

        //
        updatedRisk.setFilePath(filePath);
        updatedRisk.setProject(project);
        updatedRisk.setReportedBy(user);
        updatedRisk.setUpdatedAt(LocalDateTime.now());
        riskService.updateRisk(id, updatedRisk);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                messageSource.getMessage("edit_success", null, Locale.getDefault()));
        return "redirect:/risk/home";
    }

    @GetMapping("/delete/{id}")
    public String deleteRisk(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        riskService.deleteRisk(id);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                messageSource.getMessage("delete_success", null, Locale.getDefault()));
        return "redirect:/risk/home";
    }

    @GetMapping("/approve/{id}")
    public String appRisk(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        riskService.appRisk(id);
        Risk risk = riskService.getRiskById(id).get();
        sendNotificationUtil.sendStatusRiskNotificationToCreater(risk);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                messageSource.getMessage("approve_success", null, Locale.getDefault()));
        return "redirect:/risk/home";
    }

    @PostMapping("/upp/{id}")
    public String uppRisk(@PathVariable Long id,
                             @ModelAttribute("risk") Risk updatedRisk,
                             RedirectAttributes redirectAttributes, HttpSession session) {

        Optional<Risk> optionalRisk = riskService.getRiskById(id);
        if (optionalRisk.isEmpty()) {
            redirectAttributes.addFlashAttribute(CommonConstant.ERROR_MESSAGE,
                    messageSource.getMessage("risk_not_found", null, Locale.getDefault()));
            return "redirect:/risk/home";
        }
        User user = (User) session.getAttribute("user");
        updatedRisk.setReportedBy(user);
        updatedRisk.setUpdatedAt(LocalDateTime.now());
        riskService.updateStatusRisk(id, updatedRisk);
        Risk obj = riskService.getRiskById(id).get();
        sendNotificationUtil.sendStatusRiskNotificationToManager(obj);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                messageSource.getMessage("edit_success", null, Locale.getDefault()));
        return "redirect:/risk/home";
    }
}
