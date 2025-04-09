package com.quan_ly.spring.repositories;

import com.quan_ly.spring.models.Risk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RiskRepository extends JpaRepository<Risk, Long> {
    // Các phương thức tìm kiếm hoặc thao tác thêm có thể viết tại đây nếu cần
}
