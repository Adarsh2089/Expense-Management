package com.teaminfinity.expensemanagement.dto.expense;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class SubmitExpenseRequest {
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;
    
    @NotBlank(message = "Currency is required")
    private String currency;
    
    @NotBlank(message = "Category is required")
    private String category;
    
    private String description;
    
    @NotNull(message = "Expense date is required")
    private LocalDate expenseDate;
    
    private String receiptImageUrl; // Optional
}
