package com.gindho.pharmacy.controller;

import com.gindho.base.ApiResponse;
import com.gindho.pharmacy.model.LotMedicament;
import com.gindho.pharmacy.model.Maladie;
import com.gindho.pharmacy.model.Medicament;
import com.gindho.pharmacy.model.PatientMaladie;
import com.gindho.pharmacy.repository.LotMedicamentRepository;
import com.gindho.pharmacy.repository.MaladieRepository;
import com.gindho.pharmacy.repository.MedicamentRepository;
import com.gindho.pharmacy.repository.PatientMaladieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PharmacyController {
    private final MedicamentRepository medRepo;
    private final LotMedicamentRepository lotRepo;
    private final MaladieRepository maladieRepository;
    private final PatientMaladieRepository patientMaladieRepository;

    public PharmacyController(MedicamentRepository medRepo, LotMedicamentRepository lotRepo, MaladieRepository maladieRepository, PatientMaladieRepository patientMaladieRepository) {
        this.medRepo = medRepo;
        this.lotRepo = lotRepo;
        this.maladieRepository = maladieRepository;
        this.patientMaladieRepository = patientMaladieRepository;
    }

    @GetMapping("/api/pharmacy/medicaments")
    public ResponseEntity<ApiResponse<?>> findAll() { return ResponseEntity.ok(ApiResponse.of(medRepo.findAll())); }

    @GetMapping("/api/pharmacy/medicaments/search")
    public ResponseEntity<ApiResponse<?>> search(String medicament) { return ResponseEntity.ok(ApiResponse.of(medRepo.findByNomContainingIgnoreCase(medicament))); }

    @PostMapping("/api/pharmacy/medicaments")
    public ResponseEntity<ApiResponse<?>> create(@RequestBody Medicament m) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(medRepo.save(m)));
    }

    @PutMapping("/api/pharmacy/medicaments/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Long id, @RequestBody Medicament m) {
        Medicament existing = medRepo.findById(id).orElseThrow();
        existing.setCode(m.getCode());
        existing.setNom(m.getNom());
        existing.setForme(m.getForme());
        existing.setDosage(m.getDosage());
        existing.setVoieAdministration(m.getVoieAdministration());
        existing.setPrixUnitaire(m.getPrixUnitaire());
        existing.setStockMinimum(m.getStockMinimum());
        existing.setActif(m.isActif());
        return ResponseEntity.ok(ApiResponse.of(medRepo.save(existing)));
    }

    @DeleteMapping("/api/pharmacy/medicaments/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        medRepo.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Médicament supprimé", null));
    }

    @GetMapping("/api/pharmacy/medicaments/{id}/lots")
    public ResponseEntity<ApiResponse<?>> lots(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(lotRepo.findByMedicamentId(id)));
    }

    @PostMapping("/api/pharmacy/lots")
    public ResponseEntity<ApiResponse<?>> addLot(@RequestBody LotMedicament l) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(lotRepo.save(l)));
    }

    @PatchMapping("/api/pharmacy/lots/{id}/quantite")
    public ResponseEntity<ApiResponse<?>> updateQuantite(@PathVariable Long id, @RequestParam int quantite) {
        LotMedicament lot = lotRepo.findById(id).orElseThrow();
        lot.setQuantiteRestante(quantite);
        return ResponseEntity.ok(ApiResponse.of(lotRepo.save(lot)));
    }

    @GetMapping("/api/maladies")
    public ResponseEntity<ApiResponse<?>> listMaladies() { return ResponseEntity.ok(ApiResponse.of(maladieRepository.findAll())); }

    @PostMapping("/api/maladies")
    public ResponseEntity<ApiResponse<?>> createMaladie(@RequestBody Maladie maladie) { return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(maladieRepository.save(maladie))); }

    @PutMapping("/api/maladies/{id}")
    public ResponseEntity<ApiResponse<?>> updateMaladie(@PathVariable Long id, @RequestBody Maladie maladie) {
        Maladie existing = maladieRepository.findById(id).orElseThrow();
        existing.setCode(maladie.getCode());
        existing.setNom(maladie.getNom());
        existing.setDescription(maladie.getDescription());
        existing.setActive(maladie.isActive());
        return ResponseEntity.ok(ApiResponse.of(maladieRepository.save(existing)));
    }

    @DeleteMapping("/api/maladies/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMaladie(@PathVariable Long id) {
        maladieRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.ok("Maladie supprimée", null));
    }

    @GetMapping("/api/maladies/patient/{patientId}")
    public ResponseEntity<ApiResponse<?>> listPatientMaladies(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.of(patientMaladieRepository.findByPatientId(patientId)));
    }

    @PostMapping("/api/maladies/patient")
    public ResponseEntity<ApiResponse<?>> addPatientMaladie(@RequestBody PatientMaladie patientMaladie) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(patientMaladieRepository.save(patientMaladie)));
    }

    @GetMapping("/api/pharmacie")
    public ResponseEntity<ApiResponse<?>> listPharmacie() { return ResponseEntity.ok(ApiResponse.of(medRepo.findAll())); }

    @PostMapping("/api/pharmacie")
    public ResponseEntity<ApiResponse<?>> createPharmacie(@RequestBody Medicament m) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(medRepo.save(m)));
    }

    @PatchMapping("/api/pharmacie/{id}/quantite")
    public ResponseEntity<ApiResponse<?>> updatePharmacieQuantite(@PathVariable Long id, @RequestParam int quantite) {
        LotMedicament lot = lotRepo.findByMedicamentId(id).stream().findFirst().orElse(null);
        if (lot != null) {
            lot.setQuantiteRestante(quantite);
            return ResponseEntity.ok(ApiResponse.of(lotRepo.save(lot)));
        }
        return ResponseEntity.ok(ApiResponse.of(medRepo.findById(id).orElseThrow()));
    }

    @GetMapping("/api/pharmacie/recherche")
    public ResponseEntity<ApiResponse<?>> searchPharmacie(@RequestParam String medicament) {
        return ResponseEntity.ok(ApiResponse.of(medRepo.findByNomContainingIgnoreCase(medicament)));
    }
}
