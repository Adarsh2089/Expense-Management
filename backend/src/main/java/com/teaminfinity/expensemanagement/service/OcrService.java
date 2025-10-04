package com.teaminfinity.expensemanagement.service;

import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Random;

/**
 * OCR service stub for receipt parsing.
 * Returns mock data for MVP. In production, integrate with OCR API.
 */
@Service
public class OcrService {
    
    private final Random random = new Random();
    
    /**
     * Parse receipt image and extract fields.
     * 
     * @param receiptImage Receipt image file
     * @return Parsed receipt data
     */
    public OcrResult parseReceipt(MultipartFile receiptImage) {
        // Stub implementation - returns mock data
        // In production, integrate with OCR service (e.g., Azure Computer Vision, AWS Textract)
        
        OcrResult result = new OcrResult();
        result.setAmount(new BigDecimal("50.00").add(BigDecimal.valueOf(random.nextInt(950))));
        result.setDate(LocalDate.now().minusDays(random.nextInt(30)));
        result.setVendor(getRandomVendor());
        result.setCategory(getRandomCategory());
        result.setConfidence(0.85 + (random.nextDouble() * 0.15)); // 85-100% confidence
        
        return result;
    }
    
    private String getRandomVendor() {
        String[] vendors = {"Office Depot", "Starbucks", "Uber", "Amazon", "Hotel Marriott", "Delta Airlines"};
        return vendors[random.nextInt(vendors.length)];
    }
    
    private String getRandomCategory() {
        String[] categories = {"Office Supplies", "Meals", "Transportation", "Equipment", "Accommodation", "Travel"};
        return categories[random.nextInt(categories.length)];
    }
    
    /**
     * OCR result data structure.
     */
    @Data
    public static class OcrResult {
        private BigDecimal amount;
        private LocalDate date;
        private String vendor;
        private String category;
        private Double confidence;
    }
}
