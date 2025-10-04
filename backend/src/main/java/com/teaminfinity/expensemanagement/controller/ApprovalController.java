package com.teaminfinity.expensemanagement.controller;

import com.teaminfinity.expensemanagement.dto.approval.ApprovalDecisionRequest;
import com.teaminfinity.expensemanagement.dto.approval.ApprovalStepResponse;
import com.teaminfinity.expensemanagement.entity.ApprovalStep;
import com.teaminfinity.expensemanagement.entity.AppUser;
import com.teaminfinity.expensemanagement.service.ApprovalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for approval workflow operations.
 */
@RestController
@RequestMapping("/api/approvals")
@RequiredArgsConstructor
public class ApprovalController {
    
    private final ApprovalService approvalService;
    
    /**
     * Get pending approvals for current user.
     */
    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<ApprovalStepResponse>> getPendingApprovals(@AuthenticationPrincipal AppUser currentUser) {
        List<ApprovalStep> steps = approvalService.getPendingApprovalsForUser(currentUser.getId());
        
        List<ApprovalStepResponse> response = steps.stream()
                .map(this::mapToApprovalStepResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Get approval steps for a specific expense.
     */
    @GetMapping("/expense/{expenseId}")
    public ResponseEntity<List<ApprovalStepResponse>> getApprovalSteps(@PathVariable Long expenseId) {
        List<ApprovalStep> steps = approvalService.getApprovalStepsForExpense(expenseId);
        
        List<ApprovalStepResponse> response = steps.stream()
                .map(this::mapToApprovalStepResponse)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Approve or reject an expense.
     */
    @PutMapping("/{stepId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<ApprovalStepResponse> processApproval(@PathVariable Long stepId,
                                                                @Valid @RequestBody ApprovalDecisionRequest request,
                                                                @AuthenticationPrincipal AppUser currentUser) {
        ApprovalStep step = approvalService.processApproval(
                stepId,
                request.getDecision(),
                request.getComments(),
                currentUser.getId()
        );
        
        return ResponseEntity.ok(mapToApprovalStepResponse(step));
    }
    
    private ApprovalStepResponse mapToApprovalStepResponse(ApprovalStep step) {
        return new ApprovalStepResponse(
                step.getId(),
                step.getExpense().getId(),
                step.getApprover().getId(),
                step.getApprover().getFullName(),
                step.getSequence(),
                step.getDecision().name(),
                step.getComments(),
                step.getDecidedAt(),
                step.getCreatedAt()
        );
    }
}
