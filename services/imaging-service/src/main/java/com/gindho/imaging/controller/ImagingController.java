package com.gindho.imaging.controller;
import com.gindho.imaging.model.Imagerie; import com.gindho.imaging.repository.ImagerieRepository;
import com.gindho.base.ApiResponse; import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequiredArgsConstructor
public class ImagingController {
    private final ImagerieRepository repository;
    @GetMapping("/api/imaging/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<Imagerie>>> findByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(repository.findByPatientIdOrderByDatePrescriptionDesc(patientId)));
    }
}