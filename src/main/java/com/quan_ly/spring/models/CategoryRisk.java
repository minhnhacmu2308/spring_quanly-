package com.quan_ly.spring.models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "category_risks")
public class CategoryRisk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
