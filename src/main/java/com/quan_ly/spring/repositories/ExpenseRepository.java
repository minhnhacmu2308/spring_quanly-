package com.quan_ly.spring.repositories;

import com.quan_ly.spring.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    // Các phương thức tìm kiếm hoặc thao tác thêm có thể viết tại đây nếu cần
}
