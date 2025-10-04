package com.teaminfinity.expensemanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for fetching currency exchange rates from external API.
 * Provides fallback mock data when external API is unavailable.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateService {
    
    private final RestTemplate restTemplate;
    
    @Value("${api.external.enabled}")
    private boolean externalApiEnabled;
    
    @Value("${api.currency-rates.url}")
    private String currencyRatesApiUrl;
    
    /**
     * Get exchange rates for a base currency.
     * 
     * @param baseCurrency Base currency code (e.g., "USD")
     * @return Map of currency code to exchange rate
     */
    public Map<String, BigDecimal> getExchangeRates(String baseCurrency) {
        if (!externalApiEnabled) {
            return getMockExchangeRates(baseCurrency);
        }
        
        try {
            // Call Exchange Rate API
            String url = currencyRatesApiUrl + "/" + baseCurrency;
            @SuppressWarnings("unchecked")
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            
            Map<String, BigDecimal> rates = new HashMap<>();
            if (response != null && response.containsKey("rates")) {
                @SuppressWarnings("unchecked")
                Map<String, Object> ratesMap = (Map<String, Object>) response.get("rates");
                
                for (Map.Entry<String, Object> entry : ratesMap.entrySet()) {
                    rates.put(entry.getKey(), new BigDecimal(entry.getValue().toString()));
                }
            }
            
            return rates;
        } catch (Exception e) {
            log.error("Error fetching exchange rates from external API, using mock data", e);
            return getMockExchangeRates(baseCurrency);
        }
    }
    
    /**
     * Convert amount from one currency to another.
     */
    public BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency) {
        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        
        Map<String, BigDecimal> rates = getExchangeRates(fromCurrency);
        BigDecimal rate = rates.getOrDefault(toCurrency, BigDecimal.ONE);
        
        return amount.multiply(rate);
    }
    
    /**
     * Mock exchange rates for offline/fallback mode (base: USD).
     */
    private Map<String, BigDecimal> getMockExchangeRates(String baseCurrency) {
        Map<String, BigDecimal> mockRates = new HashMap<>();
        
        // Mock rates relative to USD
        if ("USD".equals(baseCurrency)) {
            mockRates.put("USD", BigDecimal.ONE);
            mockRates.put("EUR", new BigDecimal("0.92"));
            mockRates.put("GBP", new BigDecimal("0.79"));
            mockRates.put("INR", new BigDecimal("83.12"));
            mockRates.put("CAD", new BigDecimal("1.36"));
            mockRates.put("AUD", new BigDecimal("1.53"));
            mockRates.put("JPY", new BigDecimal("149.50"));
            mockRates.put("CNY", new BigDecimal("7.24"));
            mockRates.put("BRL", new BigDecimal("4.97"));
        } else {
            // For other base currencies, return simplified mock rates
            mockRates.put(baseCurrency, BigDecimal.ONE);
            mockRates.put("USD", BigDecimal.ONE);
        }
        
        return mockRates;
    }
}
