package com.quan_ly.spring.repositories;

import com.quan_ly.spring.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    // Các phương thức tìm kiếm hoặc thao tác thêm có thể viết tại đây nếu cần
    @Query("SELECT SUM(e.amount) FROM Expense e")
    BigDecimal sumAllExpenses();

}
