package com.teaminfinity.expensemanagement.dto.ocr;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OcrResponse {
    private BigDecimal amount;
    private LocalDate date;
    private String vendor;
    private String category;
    private Double confidence;
}
