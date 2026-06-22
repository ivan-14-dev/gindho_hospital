package com.gindho.ambulance.controller;

import com.gindho.base.ApiResponse;
import com.gindho.ambulance.model.Ambulance;
import com.gindho.ambulance.service.AmbulanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AmbulancePositionController {

    private final AmbulanceService ambulanceService;

    @PatchMapping("/api/ambulances/{id}/position")
    public ResponseEntity<ApiResponse<Ambulance>> updatePosition(
            @PathVariable Long id,
            @RequestParam Double lat,
            @RequestParam Double lng) {
        return ResponseEntity.ok(ApiResponse.ok("Position mise à jour",
                ambulanceService.updatePosition(id, lat, lng)));
    }
}
