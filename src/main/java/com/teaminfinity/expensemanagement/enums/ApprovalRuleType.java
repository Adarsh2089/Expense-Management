package com.teaminfinity.expensemanagement.enums;

/**
 * Type of approval rule for determining approvers.
 * - PERCENTAGE: Approver determined by percentage thresholds.
 * - SPECIFIC_APPROVER: Fixed approver for all expenses.
 * - HYBRID: Combination of percentage and specific approver logic.
 */
public enum ApprovalRuleType {
    PERCENTAGE,
    SPECIFIC_APPROVER,
    HYBRID
}
