package com.quan_ly.spring.services.impls;

import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.User;
import com.quan_ly.spring.repositories.ProjectRepository;
import com.quan_ly.spring.repositories.UserRepository;
import com.quan_ly.spring.services.ProjectService;
import com.quan_ly.spring.utils.EncrytedPasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectRepository projectRepository;

    public ProjectServiceImpl(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Override
    public List<Project> getAllProjects() {
        return projectRepository.findAll();
    }

    @Override
    public Optional<Project> getProjectById(Long id) {
        return projectRepository.findById(id);
    }

    @Override
    public Project createProject(Project project) {
        return projectRepository.save(project);
    }

    @Override
    public Project updateProject(Long id, Project updatedProject) {
        return projectRepository.findById(id).map(project -> {
            project.setProjectName(updatedProject.getProjectName());
            project.setBudget(updatedProject.getBudget());
            project.setStartDate(updatedProject.getStartDate());
            project.setEndDate(updatedProject.getEndDate());
            project.setStatus(updatedProject.getStatus());
            project.setAccountant(updatedProject.getAccountant());
            project.setFieldStaff(updatedProject.getFieldStaff());
            project.setPlanner(updatedProject.getPlanner());
            return projectRepository.save(project);
        }).orElseThrow(() -> new RuntimeException("Project not found with ID: " + id));
    }

    @Override
    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    @Override
    public List<Project> getProjectByUserAndDate(User user, LocalDate endDate) {
        return projectRepository.findActiveProjectsByUser(user,LocalDate.now());
    }

    @Override
    public List<Project> getProjectByUserAndDateNew(User user) {
        return projectRepository.findActiveProjectsByUserAll(user);
    }
}
