package com.quan_ly.spring.repositories;

import com.quan_ly.spring.models.CategoryRisk;
import com.quan_ly.spring.models.Document;
import com.quan_ly.spring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRiskRepository extends JpaRepository<CategoryRisk, Long> {
    Optional<CategoryRisk> findByName(String name);
    Optional<CategoryRisk> findById(Long id);
}
