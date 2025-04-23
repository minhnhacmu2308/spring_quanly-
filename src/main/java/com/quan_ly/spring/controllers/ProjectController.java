package com.quan_ly.spring.controllers;

import com.quan_ly.spring.constants.CommonConstant;
import com.quan_ly.spring.enums.Role;
import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.Expense;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.ProjectService;
import com.quan_ly.spring.services.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    MessageSource messageSource;
    @Autowired
    ProjectService projectService;

    @Autowired
    UserService userService;

    @GetMapping({"/home"})
    public String listProjects(Model model,HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("projects", projectService.getProjectByUserAndDateNew(user));
        model.addAttribute("fieldStaffs", userService.findByRoleAndNotInActiveProjects(Role.FIELD_STAFF, LocalDate.now()));
        model.addAttribute("planners", userService.findByRoleAndNotInActiveProjects(Role.PLANNER, LocalDate.now()));
        model.addAttribute("accountants", userService.findByRoleAndNotInActiveProjects(Role.ACCOUNTANT, LocalDate.now()));
        return "public/project";
    }

    @GetMapping({"/detail/{id}"})
    public String detail(@PathVariable Long id,Model model) {
        Project project = projectService.getProjectById(id).get();
        BigDecimal totalCost = project.getExpenses().stream()
                .map(Expense::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal remainingBudget = project.getBudget().subtract(totalCost);

        model.addAttribute("totalCost", totalCost);
        model.addAttribute("remainingBudget", remainingBudget);
        model.addAttribute("project", projectService.getProjectById(id).get());
        return "public/projectDetail";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("project", new Project());
        return "project/form";
    }

    @PostMapping("/create")
    public String createProject(@ModelAttribute("project") Project project, RedirectAttributes redirectAttributes,
                                HttpSession session,  @RequestParam("planner_id") Long planner_id,
                                @RequestParam("field_staff_id") Long field_staff_id,  @RequestParam("accountant_id") Long accountant_id) {
        User user = (User) session.getAttribute("user");
        User fieldStaff = (User) userService.getUserById(field_staff_id).get();
        User planner = (User) userService.getUserById(planner_id).get();
        User accountant = (User) userService.getUserById(accountant_id).get();
        project.setManager(user);
        project.setFieldStaff(fieldStaff);
        project.setPlanner(planner);
        project.setAccountant(accountant);
        projectService.createProject(project);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE, messageSource.getMessage("create_success", null, Locale.getDefault()));
        return "redirect:/project/home";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return projectService.getProjectById(id).map(project -> {
            model.addAttribute("project", project);
            return "project/form";
        }).orElse("redirect:/project/home");
    }

    @PostMapping("/edit/{id}")
    public String updateProject(@PathVariable Long id,
                                @ModelAttribute("project") Project updatedProject,
                                RedirectAttributes redirectAttributes,
                                HttpSession session,
                                @RequestParam("planner_id") Long planner_id,
                                @RequestParam("field_staff_id") Long field_staff_id,
                                @RequestParam("accountant_id") Long accountant_id) {

        Optional<Project> optionalExistingProject = projectService.getProjectById(id);

        if (optionalExistingProject.isEmpty()) {
            redirectAttributes.addFlashAttribute(CommonConstant.ERROR_MESSAGE,
                    messageSource.getMessage("project_not_found", null, Locale.getDefault()));
            return "redirect:/project/home";
        }

        User user = (User) session.getAttribute("user");
        User fieldStaff = userService.getUserById(field_staff_id).orElse(null);
        User planner = userService.getUserById(planner_id).orElse(null);
        User accountant = userService.getUserById(accountant_id).orElse(null);

        updatedProject.setProjectId(id); // đảm bảo ID đúng
        updatedProject.setManager(user);
        updatedProject.setFieldStaff(fieldStaff);
        updatedProject.setPlanner(planner);
        updatedProject.setAccountant(accountant);

        projectService.updateProject(id, updatedProject);

        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                messageSource.getMessage("edit_success", null, Locale.getDefault()));
        return "redirect:/project/home";
    }


    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<Project> optionalProject = projectService.getProjectById(id);

        if (optionalProject.isPresent()) {
            Project project = optionalProject.get();

            boolean hasChildren =
                    project.getDocuments() != null && !project.getDocuments().isEmpty() ||
                            project.getRisks() != null && !project.getRisks().isEmpty() ||
                            project.getProcesses() != null && !project.getProcesses().isEmpty() ||
                            project.getExpenses() != null && !project.getExpenses().isEmpty();

            if (hasChildren) {
                redirectAttributes.addFlashAttribute(CommonConstant.ERROR_MESSAGE,
                        "This project cannot be deleted because it has related documents, risks, processes, or expenses.");
            } else {
                projectService.deleteProject(id);
                redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                        messageSource.getMessage("delete_success", null, Locale.getDefault()));
            }
        } else {
            redirectAttributes.addFlashAttribute(CommonConstant.ERROR_MESSAGE, "Project not found!");
        }

        return "redirect:/project/home";
    }

}
