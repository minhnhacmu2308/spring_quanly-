package com.quan_ly.spring.repositories;

import com.quan_ly.spring.enums.Role;
import com.quan_ly.spring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByRole(Role role);

    List<User> findByRoleNot(Role role);

    @Query("""
        SELECT u FROM User u
        WHERE u.role = :role
        AND u.userId NOT IN (
            SELECT DISTINCT p.manager.userId FROM Project p WHERE p.endDate > :now AND p.manager IS NOT NULL
            UNION
            SELECT DISTINCT p.fieldStaff.userId FROM Project p WHERE p.endDate > :now AND p.fieldStaff IS NOT NULL
            UNION
            SELECT DISTINCT p.planner.userId FROM Project p WHERE p.endDate > :now AND p.planner IS NOT NULL
            UNION
            SELECT DISTINCT p.accountant.userId FROM Project p WHERE p.endDate > :now AND p.accountant IS NOT NULL
        )
    """)
    List<User> findByRoleAndNotInActiveProjects(@Param("role") Role role, @Param("now") LocalDate now);
}

