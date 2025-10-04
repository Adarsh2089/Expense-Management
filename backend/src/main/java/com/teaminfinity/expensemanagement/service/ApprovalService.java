package com.teaminfinity.expensemanagement.service;

import com.teaminfinity.expensemanagement.entity.ApprovalStep;
import com.teaminfinity.expensemanagement.entity.AppUser;
import com.teaminfinity.expensemanagement.entity.Expense;
import com.teaminfinity.expensemanagement.enums.ApprovalDecision;
import com.teaminfinity.expensemanagement.enums.ExpenseStatus;
import com.teaminfinity.expensemanagement.repository.ApprovalStepRepository;
import com.teaminfinity.expensemanagement.repository.ExpenseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing approval workflows.
 */
@Service
@RequiredArgsConstructor
public class ApprovalService {
    
    private final ApprovalStepRepository approvalStepRepository;
    private final ExpenseRepository expenseRepository;
    
    /**
     * Create an approval step for an expense.
     */
    @Transactional
    public ApprovalStep createApprovalStep(Expense expense, AppUser approver, int sequence) {
        ApprovalStep step = new ApprovalStep();
        step.setExpense(expense);
        step.setApprover(approver);
        step.setSequence(sequence);
        step.setDecision(ApprovalDecision.PENDING);
        
        return approvalStepRepository.save(step);
    }
    
    /**
     * Process approval or rejection decision.
     * Updates expense status if all approvals are complete or any rejection occurs.
     * 
     * @param stepId Approval step ID
     * @param decision Approval decision (APPROVED or REJECTED)
     * @param comments Optional comments
     * @param approverId ID of the approver making the decision
     * @return Updated approval step
     */
    @Transactional
    public ApprovalStep processApproval(Long stepId, ApprovalDecision decision, 
                                       String comments, Long approverId) {
        ApprovalStep step = approvalStepRepository.findById(stepId)
                .orElseThrow(() -> new RuntimeException("Approval step not found"));
        
        // Verify approver
        if (!step.getApprover().getId().equals(approverId)) {
            throw new RuntimeException("User is not authorized to approve this step");
        }
        
        // Check if already decided
        if (step.getDecision() != ApprovalDecision.PENDING) {
            throw new RuntimeException("This approval step has already been processed");
        }
        
        // Update step
        step.setDecision(decision);
        step.setComments(comments);
        step.setDecidedAt(LocalDateTime.now());
        step = approvalStepRepository.save(step);
        
        // Update expense status based on decision and workflow
        updateExpenseStatus(step.getExpense());
        
        return step;
    }
    
    /**
     * Update expense status based on approval workflow state.
     * - If any step is rejected, expense is rejected.
     * - If all steps are approved, expense is approved.
     * - Otherwise, remains pending.
     */
    private void updateExpenseStatus(Expense expense) {
        List<ApprovalStep> steps = approvalStepRepository
                .findByExpenseIdOrderBySequenceAsc(expense.getId());
        
        boolean allApproved = true;
        boolean anyRejected = false;
        
        for (ApprovalStep step : steps) {
            if (step.getDecision() == ApprovalDecision.REJECTED) {
                anyRejected = true;
                break;
            }
            if (step.getDecision() != ApprovalDecision.APPROVED) {
                allApproved = false;
            }
        }
        
        if (anyRejected) {
            expense.setStatus(ExpenseStatus.REJECTED);
        } else if (allApproved) {
            expense.setStatus(ExpenseStatus.APPROVED);
        }
        
        expenseRepository.save(expense);
    }
    
    /**
     * Get all approval steps for an expense.
     */
    public List<ApprovalStep> getApprovalStepsForExpense(Long expenseId) {
        return approvalStepRepository.findByExpenseIdOrderBySequenceAsc(expenseId);
    }
    
    /**
     * Get pending approvals for a specific approver.
     */
    public List<ApprovalStep> getPendingApprovalsForUser(Long approverId) {
        return approvalStepRepository.findPendingApprovalsByApproverId(approverId);
    }
}
