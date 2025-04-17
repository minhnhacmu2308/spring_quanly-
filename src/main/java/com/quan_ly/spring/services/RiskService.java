package com.quan_ly.spring.services;

import com.quan_ly.spring.models.Project;
import com.quan_ly.spring.models.Risk;

import java.util.List;
import java.util.Optional;

public interface RiskService {
    List<Risk> getAllRisks();
    Optional<Risk> getRiskById(Long id);
    Risk createRisk(Risk risk);
    Risk updateRisk(Long id, Risk risk);
    void deleteRisk(Long id);
    void appRisk(Long id);
    Risk updateStatusRisk(Long id, Risk risk);
}
