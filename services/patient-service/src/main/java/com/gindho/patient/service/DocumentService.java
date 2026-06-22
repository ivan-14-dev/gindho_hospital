package com.gindho.patient.service;

import com.gindho.patient.dto.DocumentDto;
import com.gindho.patient.model.DocumentPatient;
import com.gindho.patient.repository.DocumentPatientRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentService {
    private final DocumentPatientRepository repository;

    public List<DocumentDto> findByPatientId(Long patientId) {
        return repository.findByPatientId(patientId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public DocumentDto create(Long patientId, DocumentDto dto) {
        DocumentPatient document = new DocumentPatient();
        document.setPatientId(patientId);
        document.setType(dto.getType());
        document.setNom(dto.getNom());
        document.setUrl(dto.getUrl());
        document.setUploadedAt(LocalDateTime.now());
        return toDto(repository.save(document));
    }

    @Transactional
    public void delete(Long documentId) {
        if (!repository.existsById(documentId)) {
            throw new EntityNotFoundException("Document introuvable: " + documentId);
        }
        repository.deleteById(documentId);
    }

    private DocumentDto toDto(DocumentPatient document) {
        return DocumentDto.builder()
                .id(document.getId())
                .patientId(document.getPatientId())
                .type(document.getType())
                .nom(document.getNom())
                .url(document.getUrl())
                .uploadedAt(document.getUploadedAt())
                .build();
    }
}
