package com.quan_ly.spring.models;

import com.quan_ly.spring.enums.ProjectStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long projectId;

    @Column(nullable = false)
    private String projectName;

    @Column(nullable = false)
    private BigDecimal budget;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status = ProjectStatus.IN_PROGRESS; // IN_PROGRESS, COMPLETED, ON_HOLD

    @ManyToOne
    @JoinColumn(name = "manager_id")
    private User manager;

    @OneToMany(mappedBy = "project")
    private List<Document> documents;

    @OneToMany(mappedBy = "project")
    private List<Risk> risks;

    @OneToMany(mappedBy = "project")
    private List<Process> processes;

    @OneToMany(mappedBy = "project")
    private List<Expense> expenses;

}
