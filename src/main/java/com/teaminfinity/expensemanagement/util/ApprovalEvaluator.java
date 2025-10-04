package com.teaminfinity.expensemanagement.util;

import com.teaminfinity.expensemanagement.entity.AppUser;
import com.teaminfinity.expensemanagement.entity.CompanyApprovalRule;
import com.teaminfinity.expensemanagement.entity.Expense;
import com.teaminfinity.expensemanagement.enums.ApprovalRuleType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for evaluating approval rules and determining approvers.
 * Supports percentage-based, specific-approver, and hybrid rules.
 */
@Component
public class ApprovalEvaluator {
    
    /**
     * Determine the list of approvers for an expense based on company rules.
     * 
     * @param expense The expense to evaluate
     * @param rules   Active approval rules for the company (ordered by sequence)
     * @param manager The direct manager (if IS_MANAGER_APPROVER is true)
     * @param isManagerApprover Whether manager should be the first approver
     * @return Ordered list of approvers
     */
    public List<AppUser> determineApprovers(Expense expense, 
                                           List<CompanyApprovalRule> rules, 
                                           AppUser manager,
                                           boolean isManagerApprover) {
        List<AppUser> approvers = new ArrayList<>();
        
        // Add manager as first approver if flag is set and manager exists
        if (isManagerApprover && manager != null) {
            approvers.add(manager);
        }
        
        // Evaluate rules in sequence order
        for (CompanyApprovalRule rule : rules) {
            AppUser approver = evaluateRule(expense, rule);
            if (approver != null && !approvers.contains(approver)) {
                approvers.add(approver);
            }
        }
        
        return approvers;
    }
    
    /**
     * Evaluate a single approval rule against an expense.
     * 
     * @param expense The expense to evaluate
     * @param rule    The approval rule
     * @return The approver determined by this rule, or null if no match
     */
    private AppUser evaluateRule(Expense expense, CompanyApprovalRule rule) {
        switch (rule.getRuleType()) {
            case PERCENTAGE:
                return evaluatePercentageRule(expense, rule);
            case SPECIFIC_APPROVER:
                return rule.getSpecificApprover();
            case HYBRID:
                return evaluateHybridRule(expense, rule);
            default:
                return null;
        }
    }
    
    /**
     * Evaluate percentage-based rule.
     * Compares expense amount to threshold percentage.
     */
    private AppUser evaluatePercentageRule(Expense expense, CompanyApprovalRule rule) {
        if (rule.getThresholdAmount() == null || rule.getThresholdPercentage() == null) {
            return null;
        }
        
        // Calculate percentage of threshold
        BigDecimal thresholdValue = rule.getThresholdAmount()
                .multiply(rule.getThresholdPercentage())
                .divide(BigDecimal.valueOf(100));
        
        // If expense amount exceeds threshold, return the specific approver
        if (expense.getAmount().compareTo(thresholdValue) > 0) {
            return rule.getSpecificApprover();
        }
        
        return null;
    }
    
    /**
     * Evaluate hybrid rule (combination of percentage and specific approver).
     * Uses percentage logic but always returns the specific approver if threshold is met.
     */
    private AppUser evaluateHybridRule(Expense expense, CompanyApprovalRule rule) {
        // For hybrid, use same logic as percentage but always return approver if conditions met
        if (rule.getThresholdAmount() != null) {
            if (expense.getAmount().compareTo(rule.getThresholdAmount()) > 0) {
                return rule.getSpecificApprover();
            }
        }
        return null;
    }
}
