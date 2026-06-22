package com.gindho.setup.controller;

import com.gindho.setup.dto.SetupRequest;
import com.gindho.setup.service.SetupService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/setup")
@CrossOrigin(origins = "*")
public class SetupController {

    private final SetupService setupService;

    public SetupController(SetupService setupService) {
        this.setupService = setupService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getSetupStatus() {
        return ResponseEntity.ok(setupService.getSetupStatus());
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, Object>> startSetup(@Valid @RequestBody SetupRequest request) {
        return ResponseEntity.ok(setupService.startSetup(request));
    }

    @GetMapping("/progress")
    public ResponseEntity<Map<String, Object>> getProgress() {
        return ResponseEntity.ok(setupService.getProgress());
    }

    @PostMapping("/reset")
    public ResponseEntity<Map<String, Object>> resetSetup() {
        return ResponseEntity.ok(setupService.resetSetup());
    }
}