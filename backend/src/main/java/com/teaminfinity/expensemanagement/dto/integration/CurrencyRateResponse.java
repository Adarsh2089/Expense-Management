package com.teaminfinity.expensemanagement.dto.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRateResponse {
    private String baseCurrency;
    private Map<String, BigDecimal> rates; // currency code -> exchange rate
}
