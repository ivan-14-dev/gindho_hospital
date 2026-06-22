package com.gindho.event.controller;

import com.gindho.event.dto.EvenementDto;
import com.gindho.event.service.EvenementService;
import com.gindho.base.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EvenementController {

    private final EvenementService service;

    @GetMapping("/api/evenements")
    public ResponseEntity<ApiResponse<List<EvenementDto>>> list() {
        return ResponseEntity.ok(ApiResponse.okList(service.list()));
    }

    @GetMapping("/api/evenements/paginated")
    public ResponseEntity<ApiResponse<Page<EvenementDto>>> listPaginated(Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.of(service.list(pageable)));
    }

    @GetMapping("/api/evenements/type/{type}")
    public ResponseEntity<ApiResponse<List<EvenementDto>>> listByType(@PathVariable String type) {
        return ResponseEntity.ok(ApiResponse.okList(service.list().stream()
                .filter(e -> e.getTypeEvenement() != null && e.getTypeEvenement().equalsIgnoreCase(type))
                .toList()));
    }

    @GetMapping("/api/evenements/range")
    public ResponseEntity<ApiResponse<List<EvenementDto>>> listByRange(
            @RequestParam String debut, @RequestParam String fin) {
        LocalDateTime d = LocalDateTime.parse(debut);
        LocalDateTime f = LocalDateTime.parse(fin);
        return ResponseEntity.ok(ApiResponse.okList(service.list().stream()
                .filter(e -> {
                    LocalDateTime ed = e.getDateDebut();
                    return ed != null && !ed.isBefore(d) && !ed.isAfter(f);
                })
                .toList()));
    }

    @GetMapping("/api/evenements/{id}")
    public ResponseEntity<ApiResponse<EvenementDto>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Événement récupéré", service.findById(id)));
    }

    @PostMapping("/api/evenements")
    public ResponseEntity<ApiResponse<EvenementDto>> create(@Valid @RequestBody EvenementDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Événement créé", service.create(dto)));
    }

    @PatchMapping("/api/evenements/{id}/valider")
    public ResponseEntity<ApiResponse<EvenementDto>> valider(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Événement validé", service.valider(id)));
    }
}
