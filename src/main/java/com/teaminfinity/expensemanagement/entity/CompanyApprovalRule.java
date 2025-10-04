package com.teaminfinity.expensemanagement.entity;

import com.teaminfinity.expensemanagement.enums.ApprovalRuleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Company-level approval rules for expense workflow.
 * Supports percentage-based, specific-approver, or hybrid rules.
 */
@Entity
@Table(name = "company_approval_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompanyApprovalRule {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApprovalRuleType ruleType;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal thresholdAmount;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal thresholdPercentage;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specific_approver_id")
    private AppUser specificApprover;
    
    @Column(nullable = false)
    private Integer sequence;
    
    @Column(nullable = false)
    private Boolean active = true;
}
