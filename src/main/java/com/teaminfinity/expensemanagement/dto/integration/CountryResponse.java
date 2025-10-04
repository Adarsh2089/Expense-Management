package com.teaminfinity.expensemanagement.dto.integration;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryResponse {
    private Map<String, String> countries; // country name -> currency code
}
