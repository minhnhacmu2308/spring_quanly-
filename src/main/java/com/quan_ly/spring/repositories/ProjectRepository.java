package com.quan_ly.spring.repositories;

import com.quan_ly.spring.models.Project;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    // Các phương thức tìm kiếm hoặc thao tác thêm có thể viết tại đây nếu cần
}
