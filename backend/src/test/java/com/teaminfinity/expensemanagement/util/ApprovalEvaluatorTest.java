package com.teaminfinity.expensemanagement.util;

import com.teaminfinity.expensemanagement.entity.AppUser;
import com.teaminfinity.expensemanagement.entity.Company;
import com.teaminfinity.expensemanagement.entity.CompanyApprovalRule;
import com.teaminfinity.expensemanagement.entity.Expense;
import com.teaminfinity.expensemanagement.enums.ApprovalRuleType;
import com.teaminfinity.expensemanagement.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for ApprovalEvaluator utility.
 */
class ApprovalEvaluatorTest {
    
    private ApprovalEvaluator approvalEvaluator;
    private Company testCompany;
    private AppUser manager;
    private AppUser admin;
    private AppUser employee;
    private Expense testExpense;
    
    @BeforeEach
    void setUp() {
        approvalEvaluator = new ApprovalEvaluator();
        
        // Setup test company
        testCompany = new Company();
        testCompany.setId(1L);
        testCompany.setName("Test Corp");
        testCompany.setDefaultCurrency("USD");
        testCompany.setIsManagerApprover(true);
        
        // Setup users
        admin = new AppUser();
        admin.setId(1L);
        admin.setEmail("admin@test.com");
        admin.setFullName("Admin User");
        admin.setRole(Role.ADMIN);
        admin.setCompany(testCompany);
        
        manager = new AppUser();
        manager.setId(2L);
        manager.setEmail("manager@test.com");
        manager.setFullName("Manager User");
        manager.setRole(Role.MANAGER);
        manager.setCompany(testCompany);
        
        employee = new AppUser();
        employee.setId(3L);
        employee.setEmail("employee@test.com");
        employee.setFullName("Employee User");
        employee.setRole(Role.EMPLOYEE);
        employee.setCompany(testCompany);
        employee.setManager(manager);
        
        // Setup test expense
        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setUser(employee);
        testExpense.setAmount(new BigDecimal("250.00"));
        testExpense.setCurrency("USD");
    }
    
    @Test
    void testDetermineApprovers_WithManagerFirst() {
        // Setup rules
        List<CompanyApprovalRule> rules = new ArrayList<>();
        
        // Rule: Specific approver (admin) for all expenses
        CompanyApprovalRule rule1 = new CompanyApprovalRule();
        rule1.setId(1L);
        rule1.setCompany(testCompany);
        rule1.setRuleType(ApprovalRuleType.SPECIFIC_APPROVER);
        rule1.setSpecificApprover(admin);
        rule1.setSequence(1);
        rule1.setActive(true);
        rules.add(rule1);
        
        // Evaluate with manager as first approver
        List<AppUser> approvers = approvalEvaluator.determineApprovers(
                testExpense, rules, manager, true);
        
        // Verify: Manager first, then admin
        assertEquals(2, approvers.size());
        assertEquals(manager.getId(), approvers.get(0).getId());
        assertEquals(admin.getId(), approvers.get(1).getId());
    }
    
    @Test
    void testDetermineApprovers_WithoutManager() {
        // Setup rules
        List<CompanyApprovalRule> rules = new ArrayList<>();
        
        CompanyApprovalRule rule1 = new CompanyApprovalRule();
        rule1.setCompany(testCompany);
        rule1.setRuleType(ApprovalRuleType.SPECIFIC_APPROVER);
        rule1.setSpecificApprover(admin);
        rule1.setSequence(1);
        rules.add(rule1);
        
        // Evaluate without manager
        List<AppUser> approvers = approvalEvaluator.determineApprovers(
                testExpense, rules, manager, false);
        
        // Verify: Only admin
        assertEquals(1, approvers.size());
        assertEquals(admin.getId(), approvers.get(0).getId());
    }
    
    @Test
    void testDetermineApprovers_HybridRule() {
        // Setup expense with large amount
        testExpense.setAmount(new BigDecimal("1000.00"));
        
        // Setup hybrid rule
        List<CompanyApprovalRule> rules = new ArrayList<>();
        
        CompanyApprovalRule rule1 = new CompanyApprovalRule();
        rule1.setCompany(testCompany);
        rule1.setRuleType(ApprovalRuleType.HYBRID);
        rule1.setThresholdAmount(new BigDecimal("500.00"));
        rule1.setSpecificApprover(admin);
        rule1.setSequence(1);
        rules.add(rule1);
        
        // Evaluate
        List<AppUser> approvers = approvalEvaluator.determineApprovers(
                testExpense, rules, null, false);
        
        // Verify: Admin should be approver (amount > threshold)
        assertEquals(1, approvers.size());
        assertEquals(admin.getId(), approvers.get(0).getId());
    }
    
    @Test
    void testDetermineApprovers_PercentageRule() {
        // Setup percentage rule
        List<CompanyApprovalRule> rules = new ArrayList<>();
        
        CompanyApprovalRule rule1 = new CompanyApprovalRule();
        rule1.setCompany(testCompany);
        rule1.setRuleType(ApprovalRuleType.PERCENTAGE);
        rule1.setThresholdAmount(new BigDecimal("1000.00"));
        rule1.setThresholdPercentage(new BigDecimal("20.00")); // 20% of 1000 = 200
        rule1.setSpecificApprover(admin);
        rule1.setSequence(1);
        rules.add(rule1);
        
        // Test with amount (250) > threshold (200)
        testExpense.setAmount(new BigDecimal("250.00"));
        
        List<AppUser> approvers = approvalEvaluator.determineApprovers(
                testExpense, rules, null, false);
        
        // Verify: Admin should be approver
        assertEquals(1, approvers.size());
        assertEquals(admin.getId(), approvers.get(0).getId());
    }
    
    @Test
    void testDetermineApprovers_NoDuplicates() {
        // Setup rules that might return same approver
        List<CompanyApprovalRule> rules = new ArrayList<>();
        
        CompanyApprovalRule rule1 = new CompanyApprovalRule();
        rule1.setRuleType(ApprovalRuleType.SPECIFIC_APPROVER);
        rule1.setSpecificApprover(admin);
        rule1.setSequence(1);
        rules.add(rule1);
        
        CompanyApprovalRule rule2 = new CompanyApprovalRule();
        rule2.setRuleType(ApprovalRuleType.SPECIFIC_APPROVER);
        rule2.setSpecificApprover(admin);
        rule2.setSequence(2);
        rules.add(rule2);
        
        // Evaluate
        List<AppUser> approvers = approvalEvaluator.determineApprovers(
                testExpense, rules, null, false);
        
        // Verify: No duplicates
        assertEquals(1, approvers.size());
        assertEquals(admin.getId(), approvers.get(0).getId());
    }
}
