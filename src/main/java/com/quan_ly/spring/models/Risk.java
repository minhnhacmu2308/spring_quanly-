package com.quan_ly.spring.models;

import com.quan_ly.spring.enums.ApproveStatus;
import com.quan_ly.spring.enums.RiskStatus;
import com.quan_ly.spring.enums.Severity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "risks")
public class Risk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long riskId;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "reported_by", nullable = false)
    private User reportedBy;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String information;

    @Enumerated(EnumType.STRING)
    private RiskStatus status = RiskStatus.NEW; // NEW, UNDER_REVIEW, RESOLVED

    @Enumerated(EnumType.STRING)
    private Severity severity; // LOW, MEDIUM, HIGH

    @Enumerated(EnumType.STRING)
    private ApproveStatus approveStatus = ApproveStatus.UNDER_REVIEW;

    @Column(nullable = false, updatable = false)
    private LocalDateTime reportedAt = LocalDateTime.now();

    private String filePath;

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
}
