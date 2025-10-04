package com.teaminfinity.expensemanagement.controller;

import com.teaminfinity.expensemanagement.dto.expense.ExpenseResponse;
import com.teaminfinity.expensemanagement.dto.expense.SubmitExpenseRequest;
import com.teaminfinity.expensemanagement.entity.AppUser;
import com.teaminfinity.expensemanagement.entity.Expense;
import com.teaminfinity.expensemanagement.enums.ExpenseStatus;
import com.teaminfinity.expensemanagement.service.ExpenseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for expense management operations.
 */
@RestController
@RequestMapping("/api/expenses")
@RequiredArgsConstructor
public class ExpenseController {
    
    private final ExpenseService expenseService;
    
    /**
     * Submit a new expense.
     */
    @PostMapping
    public ResponseEntity<ExpenseResponse> submitExpense(@Valid @RequestBody SubmitExpenseRequest request,
                                                         @AuthenticationPrincipal AppUser currentUser) {
        Expense expense = expenseService.submitExpense(
                currentUser,
                request.getAmount(),
                request.getCurrency(),
                request.getCategory(),
                request.getDescription(),
                request.getExpenseDate(),
                request.getReceiptImageUrl()
        );
        
        return ResponseEntity.ok(mapToExpenseResponse(expense));
    }
    
    /**
     * Get all expenses for current user.
     */
    @GetMapping("/my")
    public ResponseEntity<List<ExpenseResponse>> getMyExpenses(@AuthenticationPrincipal AppUser currentUser) {
        List<Expense> expenses = expenseService.getUserExpenses(currentUser.getId());
        
        List<ExpenseResponse> response = expenses.stream()
                .map(this::mapToExpenseResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get expenses by status for current user.
     */
    @GetMapping("/my/status/{status}")
    public ResponseEntity<List<ExpenseResponse>> getMyExpensesByStatus(@PathVariable ExpenseStatus status,
                                                                       @AuthenticationPrincipal AppUser currentUser) {
        List<Expense> expenses = expenseService.getUserExpensesByStatus(currentUser.getId(), status);
        
        List<ExpenseResponse> response = expenses.stream()
                .map(this::mapToExpenseResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get expense by ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ExpenseResponse> getExpense(@PathVariable Long id) {
        Expense expense = expenseService.getExpenseById(id);
        return ResponseEntity.ok(mapToExpenseResponse(expense));
    }
    
    /**
     * Get all pending expenses for company (Admin/Manager only).
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<ExpenseResponse>> getPendingExpenses(@AuthenticationPrincipal AppUser currentUser) {
        List<Expense> expenses = expenseService.getPendingExpensesByCompany(currentUser.getCompany().getId());
        
        List<ExpenseResponse> response = expenses.stream()
                .map(this::mapToExpenseResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    private ExpenseResponse mapToExpenseResponse(Expense expense) {
        return new ExpenseResponse(
                expense.getId(),
                expense.getUser().getId(),
                expense.getUser().getFullName(),
                expense.getAmount(),
                expense.getCurrency(),
                expense.getCategory(),
                expense.getDescription(),
                expense.getExpenseDate(),
                expense.getReceiptImageUrl(),
                expense.getStatus().name(),
                expense.getCreatedAt()
        );
    }
}
