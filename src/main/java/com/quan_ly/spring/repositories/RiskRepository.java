package com.quan_ly.spring.repositories;

import com.quan_ly.spring.models.Risk;
import com.quan_ly.spring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RiskRepository extends JpaRepository<Risk, Long> {
    List<Risk> findByReportedBy(User user);
    // Lấy tất cả risk của các project có managerId cụ thể
    @Query("SELECT r FROM Risk r WHERE r.project.manager.userId = :managerId")
    List<Risk> findRisksByProjectManagerId(@Param("managerId") Long managerId);
}
