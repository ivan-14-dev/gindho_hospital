package com.gindho.appointment.controller;

import com.gindho.appointment.dto.WaitingListDto;
import com.gindho.appointment.service.WaitingListService;
import com.gindho.base.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/waiting-list")
@RequiredArgsConstructor
public class WaitingListController {

    private final WaitingListService waitingListService;

    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<ApiResponse<List<WaitingListDto>>> findByMedecin(@PathVariable Long medecinId) {
        List<WaitingListDto> result = waitingListService.findByMedecin(medecinId);
        return ResponseEntity.ok(ApiResponse.ok("Liste d'attente du médecin", result));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WaitingListDto>> create(@RequestBody WaitingListDto dto) {
        WaitingListDto result = waitingListService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Inscrit sur la liste d'attente", result));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        waitingListService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Entrée supprimée de la liste d'attente", null));
    }
}