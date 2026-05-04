package com.swp391.cclearly.controller;

import com.swp391.cclearly.dto.base.ApiResponse;
import com.swp391.cclearly.dto.response.PrescriptionAiResponse;
import com.swp391.cclearly.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/prescription-ai")
@RequiredArgsConstructor
@Tag(name = "Prescription AI", description = "APIs hỗ trợ đọc đơn kính bằng AI")
public class PrescriptionAiController {

    private final GeminiService geminiService;

    @Operation(summary = "Trích xuất thông số đơn kính từ ảnh bằng Gemini AI")
    @PostMapping(value = "/extract", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<PrescriptionAiResponse>> extractPrescription(
            @RequestParam("file") MultipartFile file) {
        PrescriptionAiResponse data = geminiService.extractPrescription(file);
        return ResponseEntity.ok(ApiResponse.success("Trích xuất thông số thành công", data));
    }
}
