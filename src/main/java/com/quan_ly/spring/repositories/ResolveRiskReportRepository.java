package com.quan_ly.spring.repositories;

import com.quan_ly.spring.models.ResolveRiskReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResolveRiskReportRepository extends JpaRepository<ResolveRiskReport, Long> {
    @Query(value = "SELECT * FROM resolve_risk_report WHERE risk_id = :riskId", nativeQuery = true)
    List<ResolveRiskReport> findByRiskId(@Param("riskId") Long riskId);
}
