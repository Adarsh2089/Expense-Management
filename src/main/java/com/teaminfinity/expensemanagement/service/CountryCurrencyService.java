package com.teaminfinity.expensemanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for fetching countries and currencies from external API.
 * Provides fallback mock data when external API is unavailable.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CountryCurrencyService {
    
    private final RestTemplate restTemplate;
    
    @Value("${api.external.enabled}")
    private boolean externalApiEnabled;
    
    @Value("${api.countries.url}")
    private String countriesApiUrl;
    
    /**
     * Get all countries with their currencies.
     * 
     * @return Map of country name to currency code
     */
    public Map<String, String> getCountriesWithCurrencies() {
        if (!externalApiEnabled) {
            return getMockCountryCurrencyData();
        }
        
        try {
            // Call REST Countries API
            List<?> countries = restTemplate.getForObject(countriesApiUrl, List.class);
            
            Map<String, String> result = new HashMap<>();
            if (countries != null) {
                for (Object countryObj : countries) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> country = (Map<String, Object>) countryObj;
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> name = (Map<String, Object>) country.get("name");
                    String countryName = (String) name.get("common");
                    
                    @SuppressWarnings("unchecked")
                    Map<String, Object> currencies = (Map<String, Object>) country.get("currencies");
                    if (currencies != null && !currencies.isEmpty()) {
                        String currencyCode = currencies.keySet().iterator().next();
                        result.put(countryName, currencyCode);
                    }
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("Error fetching countries from external API, using mock data", e);
            return getMockCountryCurrencyData();
        }
    }
    
    /**
     * Get default currency for a specific country.
     */
    public String getDefaultCurrencyForCountry(String country) {
        Map<String, String> countryCurrencies = getCountriesWithCurrencies();
        return countryCurrencies.getOrDefault(country, "USD");
    }
    
    /**
     * Mock data for offline/fallback mode.
     */
    private Map<String, String> getMockCountryCurrencyData() {
        Map<String, String> mockData = new HashMap<>();
        mockData.put("United States", "USD");
        mockData.put("United Kingdom", "GBP");
        mockData.put("India", "INR");
        mockData.put("Canada", "CAD");
        mockData.put("Australia", "AUD");
        mockData.put("Germany", "EUR");
        mockData.put("France", "EUR");
        mockData.put("Japan", "JPY");
        mockData.put("China", "CNY");
        mockData.put("Brazil", "BRL");
        return mockData;
    }
}
