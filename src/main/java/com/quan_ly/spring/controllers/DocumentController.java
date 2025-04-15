package com.quan_ly.spring.controllers;

import com.quan_ly.spring.constants.CommonConstant;
import com.quan_ly.spring.exceptions.DocumentUploadException;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.DocumentService;
import com.quan_ly.spring.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Locale;

@Controller
@RequestMapping("/user")
public class DocumentController {

    @Autowired
    MessageSource messageSource;
    @Autowired
    DocumentService documentService;
    @Autowired
    UserService userService;

    @GetMapping({"/document"})
    public String listDocuments(Model model) {
        model.addAttribute("documents", documentService.getAllDocuments());
        return "public/document";
    }

    @PostMapping("/document/add")
    public String addDocument(@RequestParam("file") MultipartFile file,
                              @RequestParam("title") String title,
                              Principal principal,
                              RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            HttpSession session = request.getSession(false);
            User user = (User) session.getAttribute("user");

            // Gọi service để lưu document
            documentService.saveDocument(file, title, user);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                    messageSource.getMessage("create_success", null, Locale.getDefault()));

            return "redirect:/user/document";
        } catch (DocumentUploadException e) {
            // Gửi lỗi lên UI bằng RedirectAttributes
            redirectAttributes.addFlashAttribute(CommonConstant.ERROR_MESSAGE, e.getMessage());
            return "redirect:/user/document"; // Điều hướng về trang upload, hiển thị lỗi
        }
    }


    @PostMapping("/document/edit/{id}")
    public String updateDocument(@PathVariable Long id,
                                 @RequestParam("title") String title,
                                 @RequestParam(value = "file", required = false) MultipartFile file,
                                 RedirectAttributes redirectAttributes) {
        documentService.updateDocument(id, title, file);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE, messageSource.getMessage("edit_success", null, Locale.getDefault()));
        return "redirect:/user/document";
    }

    @GetMapping("/document/delete/{id}")
    public String deleteDocument(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        documentService.deleteDocument(id);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE, messageSource.getMessage("delete_success", null, Locale.getDefault()));
        return "redirect:/user/document";
    }
}
