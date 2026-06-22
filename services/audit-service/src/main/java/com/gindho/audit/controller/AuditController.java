package com.gindho.audit.controller;
import com.gindho.audit.model.AuditLog; import com.gindho.audit.repository.AuditLogRepository;
import com.gindho.base.ApiResponse; import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/audit") @RequiredArgsConstructor
public class AuditController {
    private final AuditLogRepository repository;
    @GetMapping
    public ResponseEntity<ApiResponse<Page<AuditLog>>> findAll(Pageable p) {
        return ResponseEntity.ok(ApiResponse.of(repository.findAll(p)));
    }
    @GetMapping("/by-actor/{acteur}")
    public ResponseEntity<ApiResponse<?>> findByActor(@PathVariable String acteur) {
        return ResponseEntity.ok(ApiResponse.of(repository.findByActeurOrderByDateActionDesc(acteur)));
    }
}