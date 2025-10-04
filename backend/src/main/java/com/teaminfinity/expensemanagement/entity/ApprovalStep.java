package com.teaminfinity.expensemanagement.entity;

import com.teaminfinity.expensemanagement.enums.ApprovalDecision;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Represents one step in a multi-step approval workflow.
 */
@Entity
@Table(name = "approval_steps")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalStep {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approver_id", nullable = false)
    private AppUser approver;
    
    @Column(nullable = false)
    private Integer sequence;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalDecision decision = ApprovalDecision.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String comments;
    
    @Column(name = "decided_at")
    private LocalDateTime decidedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
