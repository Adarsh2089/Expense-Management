package com.teaminfinity.expensemanagement.dto.expense;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseResponse {
    private Long id;
    private Long userId;
    private String userFullName;
    private BigDecimal amount;
    private String currency;
    private String category;
    private String description;
    private LocalDate expenseDate;
    private String receiptImageUrl;
    private String status;
    private LocalDateTime createdAt;
}
