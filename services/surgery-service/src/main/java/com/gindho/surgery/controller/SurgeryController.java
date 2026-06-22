package com.gindho.surgery.controller;
import com.gindho.surgery.dto.InterventionDto; import com.gindho.surgery.model.InterventionChirurgicale;
import com.gindho.surgery.repository.InterventionRepository;
import com.gindho.base.ApiResponse; import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus; import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List; import java.util.stream.Collectors;
@RestController @RequiredArgsConstructor
public class SurgeryController {
    private final InterventionRepository repository;
    @GetMapping("/api/surgeries/patient/{patientId}")
    public ResponseEntity<ApiResponse<List<InterventionDto>>> findByPatient(@PathVariable Long patientId) {
        List<InterventionDto> list = repository.findByPatientIdOrderByDateProgrammeeDesc(patientId)
                .stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.of(list));
    }
    @PostMapping("/api/surgeries")
    public ResponseEntity<ApiResponse<InterventionDto>> create(@RequestBody InterventionDto dto) {
        InterventionChirurgicale i = new InterventionChirurgicale();
        i.setPatientId(dto.getPatientId()); i.setMedecinId(dto.getMedecinId());
        i.setSalle(dto.getSalle()); i.setDateProgrammee(dto.getDateProgrammee());
        i.setDescription(dto.getDescription()); i.setEquipe(dto.getEquipe());
        repository.save(i);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(dto));
    }
    private InterventionDto toDto(InterventionChirurgicale i) {
        return InterventionDto.builder().id(i.getId()).patientId(i.getPatientId())
                .medecinId(i.getMedecinId()).salle(i.getSalle())
                .dateProgrammee(i.getDateProgrammee()).description(i.getDescription())
                .statut(i.getStatut().name()).equipe(i.getEquipe()).build();
    }
}