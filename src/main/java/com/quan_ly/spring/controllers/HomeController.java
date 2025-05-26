package com.quan_ly.spring.controllers;

import com.quan_ly.spring.models.Notification;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.ExpenseService;
import com.quan_ly.spring.services.NotificationService;
import com.quan_ly.spring.services.ProjectService;
import com.quan_ly.spring.services.RiskService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
public class HomeController {
    @Autowired
    NotificationService notificationService;

    @Autowired
    ProjectService projectService;

    @Autowired
    ExpenseService expenseService;

    @Autowired
    RiskService riskService;

    @GetMapping({"/", "/user/home"})
    public ModelAndView index(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return new ModelAndView(new RedirectView("/auth/login"));
        }

        // Thông báo chưa đọc
        List<Notification> notifications = notificationService.getNotificationsByUser(user);
        boolean hasUnread = notifications.stream().anyMatch(n -> !Boolean.TRUE.equals(n.getIsRead()));
        model.addAttribute("hasUnread", hasUnread);

        // Tổng số dự án
        long totalProjects = projectService.countProjects();
        model.addAttribute("totalProjects", totalProjects);

        // Tổng số rủi ro
        long totalRisks = riskService.countRisks();
        model.addAttribute("totalRisks", totalRisks);

        // Tổng ngân sách (null-safe)
        BigDecimal totalBudget = Optional.ofNullable(projectService.getTotalBudget()).orElse(BigDecimal.ZERO);
        model.addAttribute("totalBudget", totalBudget);

        // Tổng chi phí (null-safe)
        BigDecimal totalExpenses = Optional.ofNullable(expenseService.getTotalExpenses()).orElse(BigDecimal.ZERO);
        model.addAttribute("totalExpenses", totalExpenses);

        return new ModelAndView("public/home");
    }
}
