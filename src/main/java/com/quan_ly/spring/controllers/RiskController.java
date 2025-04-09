package com.quan_ly.spring.controllers;

import com.quan_ly.spring.constants.CommonConstant;
import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.Risk;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.ProjectService;
import com.quan_ly.spring.services.RiskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.Locale;
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

    @GetMapping("/home")
    public String listRisks(Model model) {
        model.addAttribute("risks", riskService.getAllRisks());
        model.addAttribute("projects", projectService.getAllProjects());
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
                             HttpSession session, HttpServletRequest request) {

        User user = (User) session.getAttribute("user");
        risk.setReportedBy(user);
        risk.setReportedAt(LocalDateTime.now());
        risk.setUpdatedAt(LocalDateTime.now());
        Long projectId = Long.parseLong(request.getParameter("projectId"));
        risk.setProject(projectService.getProjectById(projectId).get());
        riskService.createRisk(risk);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                messageSource.getMessage("create_success", null, Locale.getDefault()));
        return "redirect:/risk/home";
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
}
