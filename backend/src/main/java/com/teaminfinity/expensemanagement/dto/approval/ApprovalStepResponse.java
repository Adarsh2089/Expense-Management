package com.teaminfinity.expensemanagement.dto.approval;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalStepResponse {
    private Long id;
    private Long expenseId;
    private Long approverId;
    private String approverName;
    private Integer sequence;
    private String decision;
    private String comments;
    private LocalDateTime decidedAt;
    private LocalDateTime createdAt;
}
