package com.quan_ly.spring.services;

import com.quan_ly.spring.models.Project;

import java.util.List;
import java.util.Optional;
public interface ProjectService {
    List<Project> getAllProjects();
    Optional<Project> getProjectById(Long id);
    Project createProject(Project project);
    Project updateProject(Long id, Project project);
    void deleteProject(Long id);
}
