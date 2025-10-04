package com.teaminfinity.expensemanagement.repository;

import com.teaminfinity.expensemanagement.entity.Expense;
import com.teaminfinity.expensemanagement.enums.ExpenseStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {
    List<Expense> findByUserId(Long userId);
    List<Expense> findByUserIdAndStatus(Long userId, ExpenseStatus status);
    
    @Query("SELECT e FROM Expense e WHERE e.user.company.id = :companyId AND e.status = :status")
    List<Expense> findByCompanyIdAndStatus(@Param("companyId") Long companyId, @Param("status") ExpenseStatus status);
}
