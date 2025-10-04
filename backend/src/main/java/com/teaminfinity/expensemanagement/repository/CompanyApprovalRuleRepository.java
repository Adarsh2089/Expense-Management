package com.teaminfinity.expensemanagement.repository;

import com.teaminfinity.expensemanagement.entity.CompanyApprovalRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompanyApprovalRuleRepository extends JpaRepository<CompanyApprovalRule, Long> {
    List<CompanyApprovalRule> findByCompanyIdAndActiveOrderBySequenceAsc(Long companyId, Boolean active);
}
