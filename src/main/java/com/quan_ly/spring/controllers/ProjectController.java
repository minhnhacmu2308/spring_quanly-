package com.quan_ly.spring.controllers;

import com.quan_ly.spring.constants.CommonConstant;
import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.services.ProjectService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Locale;
import java.util.Optional;

@Controller
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    MessageSource messageSource;
    @Autowired
    ProjectService projectService;

    @GetMapping({"/home"})
    public String listProjects(Model model) {
        model.addAttribute("projects", projectService.getAllProjects());
        return "public/project";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("project", new Project());
        return "project/form";
    }

    @PostMapping("/create")
    public String createProject(@ModelAttribute("project") Project project, RedirectAttributes redirectAttributes, HttpSession session) {
        User user = (User) session.getAttribute("user");
        project.setManager(user);
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
                             Model model) {

        Optional<Project> optionalExistingProject = projectService.getProjectById(id);

        if (optionalExistingProject.isEmpty()) {
            redirectAttributes.addFlashAttribute(CommonConstant.ERROR_MESSAGE,messageSource.getMessage("project_not_found", null, Locale.getDefault()));
            return "redirect:/project/home";
        }

        projectService.updateProject(id, updatedProject);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE, messageSource.getMessage("edit_success", null, Locale.getDefault()));
        return "redirect:/project/home";
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        projectService.deleteProject(id);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE, messageSource.getMessage("delete_success", null, Locale.getDefault()));
        return "redirect:/project/home";
    }
}
