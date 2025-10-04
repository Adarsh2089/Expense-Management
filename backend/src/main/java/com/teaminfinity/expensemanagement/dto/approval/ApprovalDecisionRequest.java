package com.teaminfinity.expensemanagement.dto.approval;

import com.teaminfinity.expensemanagement.enums.ApprovalDecision;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApprovalDecisionRequest {
    @NotNull(message = "Decision is required")
    private ApprovalDecision decision;
    
    private String comments; // Optional
}
