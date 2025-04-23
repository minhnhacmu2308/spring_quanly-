package com.quan_ly.spring.repositories;

import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    @Query("SELECT p FROM Project p WHERE p.endDate > :now AND (p.manager = :user OR p.fieldStaff = :user OR p.planner = :user OR p.accountant = :user)")
    List<Project> findActiveProjectsByUser(User user, LocalDate now);

    @Query("SELECT p FROM Project p WHERE (p.manager = :user OR p.fieldStaff = :user OR p.planner = :user OR p.accountant = :user)")
    List<Project> findActiveProjectsByUserAll(User user);
}
