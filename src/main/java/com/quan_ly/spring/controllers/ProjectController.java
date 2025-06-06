package com.quan_ly.spring.controllers;

import com.quan_ly.spring.constants.CommonConstant;
import com.quan_ly.spring.enums.ApproveStatus;
import com.quan_ly.spring.enums.Priority;
import com.quan_ly.spring.enums.Role;
import com.quan_ly.spring.enums.Severity;
import com.quan_ly.spring.models.*;
import com.quan_ly.spring.repositories.CategoryRiskRepository;
import com.quan_ly.spring.services.NotificationService;
import com.quan_ly.spring.services.ProjectService;
import com.quan_ly.spring.services.RiskService;
import com.quan_ly.spring.services.UserService;
import com.quan_ly.spring.utils.SendNotificationUtil;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/user/project")
public class ProjectController {
    @Autowired
    MessageSource messageSource;
    @Autowired
    ProjectService projectService;

    @Autowired
    SendNotificationUtil sendNotificationUtil;

    @Autowired
    NotificationService notificationService;
    @Autowired
    UserService userService;

    @Autowired
    CategoryRiskRepository categoryRiskRepository;

    @Autowired
    RiskService riskService;

    @GetMapping({""})
    public String listProjects(Model model,HttpSession session) {
        User user = (User) session.getAttribute("user");
        model.addAttribute("projects", projectService.getProjectByUserAndDateNew(user));
        model.addAttribute("fieldStaffs", userService.findByRoleAndNotInActiveProjects(Role.FIELD_STAFF, LocalDate.now()));
        model.addAttribute("planners", userService.findByRoleAndNotInActiveProjects(Role.PLANNER, LocalDate.now()));
        model.addAttribute("accountants", userService.findByRoleAndNotInActiveProjects(Role.ACCOUNTANT, LocalDate.now()));
        model.addAttribute("riskSolvers", userService.findByRoleAndNotInActiveProjects(Role.RISK_SOLVER, LocalDate.now()));

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
    public String createProject(@ModelAttribute("project") Project project,
                                RedirectAttributes redirectAttributes,
                                HttpSession session,
                                @RequestParam("planner_id") Long planner_id,
                                @RequestParam("field_staff_id") Long field_staff_id,
                                @RequestParam("accountant_id") Long accountant_id,
                                @RequestParam("riskSolver_id") Long riskSolver_id) {

        User manager = (User) session.getAttribute("user");
        User fieldStaff = userService.getUserById(field_staff_id).orElse(null);
        User planner = userService.getUserById(planner_id).orElse(null);
        User accountant = userService.getUserById(accountant_id).orElse(null);
        User riskSolver = userService.getUserById(riskSolver_id).orElse(null);

        project.setManager(manager);
        project.setFieldStaff(fieldStaff);
        project.setPlanner(planner);
        project.setAccountant(accountant);
        project.setRiskSolver(riskSolver);
        projectService.createProject(project);

        List<Risk> risks = project.getRisks();
        if (risks != null && !risks.isEmpty()) {
            CategoryRisk categoryRisk = categoryRiskRepository.findByName("Foreseeable Risk").get();
            for (Risk risk : risks) {
                risk.setCategoryRisk(categoryRisk);
                risk.setProject(project);
                risk.setApproveStatus(ApproveStatus.APPROVED);
                risk.setSeverity(Severity.LOW);
                risk.setReportedBy(manager);
                riskService.createRisk(risk);
            }
        }

        // Send notifications to 3 assigned users
        sendNotificationUtil.sendNotificationToAssignee(fieldStaff, "Field Staff", project);
        sendNotificationUtil.sendNotificationToAssignee(planner, "Planner", project);
        sendNotificationUtil.sendNotificationToAssignee(accountant, "Accountant", project);
        sendNotificationUtil.sendNotificationToAssignee(riskSolver, "Risk Solver", project);

        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                messageSource.getMessage("create_success", null, Locale.getDefault()));

        return "redirect:/user/project";
    }


    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        return projectService.getProjectById(id).map(project -> {
            model.addAttribute("project", project);
            return "project/form";
        }).orElse("redirect:/user/project");
    }

    @PostMapping("/edit/{id}")
    public String updateProject(@PathVariable Long id,
                                @ModelAttribute("project") Project updatedProject,
                                RedirectAttributes redirectAttributes,
                                HttpSession session,
                                @RequestParam("planner_id") Long plannerId,
                                @RequestParam("field_staff_id") Long fieldStaffId,
                                @RequestParam("accountant_id") Long accountantId,
                                @RequestParam("riskSolver_id") Long riskSolver_id) {

        Optional<Project> optOld = projectService.getProjectById(id);
        if (optOld.isEmpty()) {
            redirectAttributes.addFlashAttribute(CommonConstant.ERROR_MESSAGE,
                    messageSource.getMessage("project_not_found", null, Locale.getDefault()));
            return "redirect:/user/project";
        }

        /* ---------- Resolve users ---------- */
        User manager     = (User) session.getAttribute("user");
        User newPlanner  = userService.getUserById(plannerId).orElse(null);
        User newField    = userService.getUserById(fieldStaffId).orElse(null);
        User newAccount  = userService.getUserById(accountantId).orElse(null);
        User newRiskSolver  = userService.getUserById(riskSolver_id).orElse(null);

        Project old = optOld.get(); // old snapshot

        /* ---------- Detect changes & notify ---------- */
        handleAssigneeChange(old.getPlanner(),    newPlanner,  "Planner",     old);
        handleAssigneeChange(old.getFieldStaff(), newField,    "Field Staff", old);
        handleAssigneeChange(old.getAccountant(), newAccount,  "Accountant",  old);
        handleAssigneeChange(old.getRiskSolver(), newRiskSolver,  "Risk Solver",  old);
        List<Risk> updatedRisks = Optional.ofNullable(updatedProject.getRisks()).orElse(Collections.emptyList());
        List<Long> riskIds = updatedRisks.stream()
                .map(Risk::getRiskId)
                .collect(Collectors.toList());

        List<Risk> risks = optOld.get().getRisks().stream()
                .filter(r -> riskIds.contains(r.getRiskId()))
                .collect(Collectors.toList());

        risks.forEach(oldRisk ->
                updatedRisks.stream()
                        .filter(updated -> Objects.equals(updated.getRiskId(), oldRisk.getRiskId()))
                        .findFirst()
                        .ifPresent(updated -> {
                            oldRisk.setInformation(updated.getInformation());
                            oldRisk.setSolution(updated.getSolution());
                        })
        );


        /* ---------- Filter risks: only keep categoryRiskId == 1 ---------- */
//        if (updatedProject.getRisks() != null) {
//            for(long i = 0 ; i< old.getRisks().size() ; i++){
//                for(long j = 0 ; j < updatedProject.getRisks().size() ; j++){
//                    if(old.getRisks().get(i).get)
//                }
//            }
////            List<Risk> filteredRisks = updatedProject.getRisks().stream()
////                    .filter(risk -> risk.getCategoryRisk()!= null && risk.getCategoryRisk().getId() == 1)
////                    .collect(Collectors.toList());
//            updatedProject.setRisks(updatedProject.getRisks());
//        }



        /* ---------- Persist update ---------- */
        updatedProject.setProjectId(id);
        updatedProject.setManager(manager);
        updatedProject.setPlanner(newPlanner);
        updatedProject.setFieldStaff(newField);
        updatedProject.setAccountant(newAccount);
        updatedProject.setRiskSolver(newRiskSolver);
        updatedProject.setRisks(risks);

        projectService.updateProject(id, updatedProject);

        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                messageSource.getMessage("edit_success", null, Locale.getDefault()));
        return "redirect:/user/project";
    }

    private void handleAssigneeChange(User oldUser,
                                      User newUser,
                                      String role,
                                      Project project) {

        // 1️⃣ removed?
        if (oldUser != null && !oldUser.equals(newUser)) {
            Notification n = new Notification();
            n.setTitle("You have been removed from a project");
            n.setContent(String.format(
                    "You are no longer assigned to project \"%s\" as %s.",
                    project.getProjectName(), role));
            n.setUser(oldUser);
            n.setPriority(Priority.LOW);
            notificationService.saveNotification(n);
            sendNotificationUtil.sendNotificationToUser(oldUser.getEmail(), n);
        }

        // 2️⃣ newly assigned?
        if (newUser != null && !newUser.equals(oldUser)) {
            Notification n = new Notification();
            n.setTitle("You have been assigned to a project");
            n.setContent(SendNotificationUtil
                    .buildProjectNotificationContent(project, role));
            n.setUser(newUser);
            n.setPriority(Priority.LOW);
            notificationService.saveNotification(n);
            sendNotificationUtil.sendNotificationToUser(newUser.getEmail(), n);
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteProject(@PathVariable Long id,
                                RedirectAttributes redirectAttributes) {

        Optional<Project> opt = projectService.getProjectById(id);

        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute(CommonConstant.ERROR_MESSAGE, "Project not found!");
            return "redirect:/user/project";
        }

        Project project = opt.get();

        /* ---- guard: has children? ---- */
        boolean hasChildren =
                (project.getDocuments()!=null && !project.getDocuments().isEmpty()) ||
                        (project.getRisks()!=null     && !project.getRisks().isEmpty())     ||
                        (project.getProcesses()!=null && !project.getProcesses().isEmpty()) ||
                        (project.getExpenses()!=null  && !project.getExpenses().isEmpty());

        if (hasChildren) {
            redirectAttributes.addFlashAttribute(CommonConstant.ERROR_MESSAGE,
                    "This project cannot be deleted because it has related documents, risks, processes, or expenses.");
            return "redirect:/user/project";
        }

        /* ===================================================================== */
        /* 1️⃣  Send “project deleted” notifications BEFORE deleting in DB        */
        /* ===================================================================== */
        sendProjectDeletedToast(project.getManager(),   project);
        sendProjectDeletedToast(project.getPlanner(),   project);
        sendProjectDeletedToast(project.getFieldStaff(),project);
        sendProjectDeletedToast(project.getAccountant(),project);

        /* 2️⃣  delete  */
        projectService.deleteProject(id);
        redirectAttributes.addFlashAttribute(CommonConstant.SUCCESS_MESSAGE,
                messageSource.getMessage("delete_success", null, Locale.getDefault()));

        return "redirect:/user/project";
    }

    /* ----------------------------------------------------------------------- */
    /* helper: build & push a deletion notice to a single user                 */
    private void sendProjectDeletedToast(User user, Project project){
        if(user==null) return;

        Notification n = new Notification();
        n.setTitle("Project removed");
        n.setContent(String.format(
                "The project \"%s\" you were participating in has been deleted.",
                project.getProjectName()));
        n.setUser(user);
        n.setPriority(Priority.MEDIUM);
        notificationService.saveNotification(n);
        sendNotificationUtil.sendNotificationToUser(user.getEmail(), n);
    }
}
