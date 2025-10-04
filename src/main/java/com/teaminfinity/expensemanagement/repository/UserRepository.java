package com.teaminfinity.expensemanagement.repository;

import com.teaminfinity.expensemanagement.entity.AppUser;
import com.teaminfinity.expensemanagement.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByEmail(String email);
    List<AppUser> findByCompanyId(Long companyId);
    List<AppUser> findByCompanyIdAndRole(Long companyId, Role role);
    boolean existsByEmail(String email);
}
