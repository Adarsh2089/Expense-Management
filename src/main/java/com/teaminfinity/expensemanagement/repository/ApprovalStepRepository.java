package com.teaminfinity.expensemanagement.repository;

import com.teaminfinity.expensemanagement.entity.ApprovalStep;
import com.teaminfinity.expensemanagement.enums.ApprovalDecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApprovalStepRepository extends JpaRepository<ApprovalStep, Long> {
    List<ApprovalStep> findByExpenseIdOrderBySequenceAsc(Long expenseId);
    List<ApprovalStep> findByApproverIdAndDecision(Long approverId, ApprovalDecision decision);
    
    @Query("SELECT a FROM ApprovalStep a WHERE a.approver.id = :approverId AND a.decision = 'PENDING' ORDER BY a.createdAt DESC")
    List<ApprovalStep> findPendingApprovalsByApproverId(@Param("approverId") Long approverId);
}
