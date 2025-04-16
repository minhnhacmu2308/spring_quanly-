package com.quan_ly.spring.services.impls;

import com.quan_ly.spring.models.Risk;
import com.quan_ly.spring.repositories.RiskRepository;
import com.quan_ly.spring.services.RiskService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RiskServiceImpl implements RiskService {
    private final RiskRepository riskRepository;

    public RiskServiceImpl(RiskRepository riskRepository) {
        this.riskRepository = riskRepository;
    }

    @Override
    public List<Risk> getAllRisks() {
        return riskRepository.findAll();
    }

    @Override
    public Optional<Risk> getRiskById(Long id) {
        return riskRepository.findById(id);
    }

    @Override
    public Risk createRisk(Risk risk) {
        return riskRepository.save(risk);
    }

    @Override
    public Risk updateRisk(Long id, Risk updatedRisk) {
        return riskRepository.findById(id).map(risk -> {
            risk.setProject(updatedRisk.getProject());
            risk.setReportedBy(updatedRisk.getReportedBy());
            risk.setInformation(updatedRisk.getInformation());
            risk.setStatus(updatedRisk.getStatus());
            risk.setSeverity(updatedRisk.getSeverity());
            risk.setFilePath(updatedRisk.getFilePath());
            risk.setUpdatedAt(updatedRisk.getUpdatedAt());
            return riskRepository.save(risk);
        }).orElseThrow(() -> new RuntimeException("Risk not found with ID: " + id));
    }

    @Override
    public void deleteRisk(Long id) {
        riskRepository.deleteById(id);
    }
}
