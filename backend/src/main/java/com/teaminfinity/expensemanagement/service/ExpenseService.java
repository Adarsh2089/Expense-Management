package com.teaminfinity.expensemanagement.service;

import com.teaminfinity.expensemanagement.entity.ApprovalStep;
import com.teaminfinity.expensemanagement.entity.AppUser;
import com.teaminfinity.expensemanagement.entity.CompanyApprovalRule;
import com.teaminfinity.expensemanagement.entity.Expense;
import com.teaminfinity.expensemanagement.enums.ExpenseStatus;
import com.teaminfinity.expensemanagement.repository.CompanyApprovalRuleRepository;
import com.teaminfinity.expensemanagement.repository.ExpenseRepository;
import com.teaminfinity.expensemanagement.util.ApprovalEvaluator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Service for expense management operations.
 */
@Service
@RequiredArgsConstructor
public class ExpenseService {
    
    private final ExpenseRepository expenseRepository;
    private final CompanyApprovalRuleRepository approvalRuleRepository;
    private final ApprovalService approvalService;
    private final ApprovalEvaluator approvalEvaluator;
    
    /**
     * Submit a new expense.
     * Automatically creates approval steps based on company rules.
     * 
     * @param user User submitting the expense
     * @param amount Expense amount
     * @param currency Currency code
     * @param category Expense category
     * @param description Description
     * @param expenseDate Date of expense
     * @param receiptImageUrl Receipt image URL (optional)
     * @return Created expense
     */
    @Transactional
    public Expense submitExpense(AppUser user, BigDecimal amount, String currency,
                                String category, String description, LocalDate expenseDate,
                                String receiptImageUrl) {
        // Create expense
        Expense expense = new Expense();
        expense.setUser(user);
        expense.setAmount(amount);
        expense.setCurrency(currency);
        expense.setCategory(category);
        expense.setDescription(description);
        expense.setExpenseDate(expenseDate);
        expense.setReceiptImageUrl(receiptImageUrl);
        expense.setStatus(ExpenseStatus.PENDING);
        
        expense = expenseRepository.save(expense);
        
        // Get company approval rules
        List<CompanyApprovalRule> rules = approvalRuleRepository
                .findByCompanyIdAndActiveOrderBySequenceAsc(user.getCompany().getId(), true);
        
        // Determine approvers using ApprovalEvaluator
        List<AppUser> approvers = approvalEvaluator.determineApprovers(
                expense, 
                rules, 
                user.getManager(), 
                user.getCompany().getIsManagerApprover()
        );
        
        // Create approval steps
        for (int i = 0; i < approvers.size(); i++) {
            approvalService.createApprovalStep(expense, approvers.get(i), i + 1);
        }
        
        return expense;
    }
    
    /**
     * Get all expenses for a user.
     */
    public List<Expense> getUserExpenses(Long userId) {
        return expenseRepository.findByUserId(userId);
    }
    
    /**
     * Get expenses by status for a user.
     */
    public List<Expense> getUserExpensesByStatus(Long userId, ExpenseStatus status) {
        return expenseRepository.findByUserIdAndStatus(userId, status);
    }
    
    /**
     * Get expense by ID.
     */
    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
    }
    
    /**
     * Get all pending expenses for a company (Admin/Manager view).
     */
    public List<Expense> getPendingExpensesByCompany(Long companyId) {
        return expenseRepository.findByCompanyIdAndStatus(companyId, ExpenseStatus.PENDING);
    }
}
