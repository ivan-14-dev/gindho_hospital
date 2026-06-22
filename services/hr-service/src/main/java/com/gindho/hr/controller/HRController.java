package com.gindho.hr.controller;

import com.gindho.base.ApiResponse;
import com.gindho.hr.dto.CongeDto;
import com.gindho.hr.dto.EmployeDto;
import com.gindho.hr.dto.PresenceDto;
import com.gindho.hr.model.Conge;
import com.gindho.hr.model.Employe;
import com.gindho.hr.service.HrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hr")
@RequiredArgsConstructor
public class HRController {
    private final HrService hrService;

    @GetMapping("/employees")
    public ResponseEntity<ApiResponse<Object>> findAll() {
        return ResponseEntity.ok(ApiResponse.of(hrService.findAll()));
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<ApiResponse<EmployeDto>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(hrService.findById(id)));
    }

    @PostMapping("/employees")
    public ResponseEntity<ApiResponse<EmployeDto>> create(@RequestBody Employe e) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(hrService.create(e)));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<ApiResponse<EmployeDto>> update(@PathVariable Long id, @RequestBody Employe e) {
        return ResponseEntity.ok(ApiResponse.of(hrService.update(id, e)));
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        hrService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Employé supprimé", null));
    }

    @GetMapping("/doctors")
    public ResponseEntity<ApiResponse<Object>> findDoctors() {
        return ResponseEntity.ok(ApiResponse.of(hrService.findDoctors()));
    }

    @GetMapping("/doctors/{id}")
    public ResponseEntity<ApiResponse<EmployeDto>> findDoctor(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(hrService.findById(id)));
    }

    @GetMapping("/doctors/by-user/{userId}")
    public ResponseEntity<ApiResponse<EmployeDto>> findByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.of(hrService.findByUserId(userId)));
    }

    @PostMapping("/presence/pointer/{employeId}")
    public ResponseEntity<ApiResponse<PresenceDto>> pointer(@PathVariable Long employeId) {
        return ResponseEntity.ok(ApiResponse.of(hrService.pointer(employeId)));
    }

    @GetMapping("/presence/{employeId}")
    public ResponseEntity<ApiResponse<Object>> listPresences(@PathVariable Long employeId) {
        return ResponseEntity.ok(ApiResponse.of(hrService.listPresences(employeId)));
    }

    @PostMapping("/conges")
    public ResponseEntity<ApiResponse<CongeDto>> createConge(@RequestBody Conge conge) {
        return ResponseEntity.ok(ApiResponse.of(hrService.createConge(conge)));
    }

    @GetMapping("/conges/{employeId}")
    public ResponseEntity<ApiResponse<Object>> listConges(@PathVariable Long employeId) {
        return ResponseEntity.ok(ApiResponse.of(hrService.listConges(employeId)));
    }

    @PostMapping("/conges/{id}/valider")
    public ResponseEntity<ApiResponse<CongeDto>> valider(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.of(hrService.valider(id)));
    }
}
