package com.quan_ly.spring.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "resolve_risk_report")
public class ResolveRiskReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resolveRiskReportId;

    @ManyToOne
    @JoinColumn(name = "risk_id", nullable = true)
    private Risk risk;

    @ManyToOne
    @JoinColumn(name = "resolved_by", nullable = false)
    private User resolvedBy;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String filePath;

    @Column(nullable = false, updatable = false)
    private LocalDateTime resolvedDate = LocalDateTime.now();

}
