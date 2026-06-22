package com.gindho.imaging.controller;

import com.gindho.base.ApiResponse;
import com.gindho.imaging.model.Imagerie;
import com.gindho.imaging.repository.ImagerieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
public class ImagerieAliasController {

    private final ImagerieRepository repository;

    @PostMapping("/api/imagerie")
    public ResponseEntity<ApiResponse<Imagerie>> create(@RequestBody Imagerie ei) {
        ei.setDatePrescription(LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Examen créé", repository.save(ei)));
    }
}
