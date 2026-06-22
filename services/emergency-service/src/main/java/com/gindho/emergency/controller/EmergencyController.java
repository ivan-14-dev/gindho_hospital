package com.gindho.emergency.controller;

import com.gindho.emergency.dto.EmergencyTriageDto;
import com.gindho.emergency.service.EmergencyService;
import com.gindho.base.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController @RequiredArgsConstructor
public class EmergencyController {
    private final EmergencyService emergencyService;

    @PostMapping("/api/emergency/triage")
    public ResponseEntity<ApiResponse<?>> triage(@Valid @RequestBody EmergencyTriageDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(emergencyService.trier(dto)));
    }
}