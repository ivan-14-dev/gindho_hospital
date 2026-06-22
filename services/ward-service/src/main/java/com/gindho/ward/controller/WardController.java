package com.gindho.ward.controller;
import com.gindho.ward.dto.WardDto;
import com.gindho.ward.model.Ward;
import com.gindho.ward.repository.WardRepository;
import com.gindho.base.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class WardController {
    private final WardRepository repository;

    @GetMapping("/api/wards")
    public ResponseEntity<ApiResponse<List<WardDto>>> findAll() {
        List<WardDto> list = repository.findAll().stream().map(w -> WardDto.builder().id(w.getId()).code(w.getCode())
                .nom(w.getNom()).specialite(w.getSpecialite()).capaciteLits(w.getCapaciteLits())
                .chefService(w.getChefService()).telephone(w.getTelephone()).build()).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.of(list));
    }

    @PostMapping("/api/wards")
    public ResponseEntity<ApiResponse<WardDto>> create(@RequestBody WardDto dto) {
        Ward w = new Ward(); w.setCode(dto.getCode()); w.setNom(dto.getNom());
        w.setSpecialite(dto.getSpecialite()); w.setCapaciteLits(dto.getCapaciteLits());
        w.setChefService(dto.getChefService()); w.setTelephone(dto.getTelephone());
        repository.save(w);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(dto));
    }

    @GetMapping("/api/wards/{id}")
    public ResponseEntity<ApiResponse<WardDto>> findById(@PathVariable Long id) {
        Ward w = repository.findById(id).orElseThrow();
        return ResponseEntity.ok(ApiResponse.of(WardDto.builder()
                .id(w.getId()).code(w.getCode()).nom(w.getNom())
                .specialite(w.getSpecialite()).capaciteLits(w.getCapaciteLits())
                .chefService(w.getChefService()).telephone(w.getTelephone()).build()));
    }
}
