package com.teaminfinity.expensemanagement.controller;

import com.teaminfinity.expensemanagement.dto.ocr.OcrResponse;
import com.teaminfinity.expensemanagement.service.OcrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for OCR operations.
 */
@RestController
@RequestMapping("/api/ocr")
@RequiredArgsConstructor
public class OcrController {
    
    private final OcrService ocrService;
    
    /**
     * Parse receipt image and extract expense details.
     */
    @PostMapping("/parse-receipt")
    public ResponseEntity<OcrResponse> parseReceipt(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        OcrService.OcrResult result = ocrService.parseReceipt(file);
        
        OcrResponse response = new OcrResponse(
                result.getAmount(),
                result.getDate(),
                result.getVendor(),
                result.getCategory(),
                result.getConfidence()
        );
        
        return ResponseEntity.ok(response);
    }
}
