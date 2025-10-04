package com.teaminfinity.expensemanagement.controller;

import com.teaminfinity.expensemanagement.dto.integration.CountryResponse;
import com.teaminfinity.expensemanagement.dto.integration.CurrencyRateResponse;
import com.teaminfinity.expensemanagement.service.CountryCurrencyService;
import com.teaminfinity.expensemanagement.service.CurrencyRateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Controller for external integration endpoints (countries, currencies, rates).
 */
@RestController
@RequestMapping("/api/integration")
@RequiredArgsConstructor
public class IntegrationController {
    
    private final CountryCurrencyService countryCurrencyService;
    private final CurrencyRateService currencyRateService;
    
    /**
     * Get all countries with their default currencies.
     */
    @GetMapping("/countries")
    public ResponseEntity<CountryResponse> getCountries() {
        Map<String, String> countries = countryCurrencyService.getCountriesWithCurrencies();
        return ResponseEntity.ok(new CountryResponse(countries));
    }
    
    /**
     * Get currency exchange rates for a base currency.
     */
    @GetMapping("/currency-rates/{baseCurrency}")
    public ResponseEntity<CurrencyRateResponse> getCurrencyRates(@PathVariable String baseCurrency) {
        Map<String, BigDecimal> rates = currencyRateService.getExchangeRates(baseCurrency);
        return ResponseEntity.ok(new CurrencyRateResponse(baseCurrency, rates));
    }
}
