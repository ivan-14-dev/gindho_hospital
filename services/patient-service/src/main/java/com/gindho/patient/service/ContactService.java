package com.gindho.patient.service;

import com.gindho.patient.dto.AssurancePatientDto;
import com.gindho.patient.dto.ContactDto;
import com.gindho.patient.model.AssurancePatient;
import com.gindho.patient.model.Contact;
import com.gindho.patient.repository.AssurancePatientRepository;
import com.gindho.patient.repository.ContactRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContactService {
    private final ContactRepository repository;

    public List<ContactDto> findByPatientId(Long patientId) {
        return repository.findByPatientId(patientId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public ContactDto create(Long patientId, ContactDto dto) {
        Contact contact = new Contact();
        contact.setPatientId(patientId);
        contact.setNom(dto.getNom());
        contact.setTelephone(dto.getTelephone());
        contact.setRelation(dto.getRelation());
        contact.setEmail(dto.getEmail());
        return toDto(repository.save(contact));
    }

    @Transactional
    public ContactDto update(Long contactId, ContactDto dto) {
        Contact contact = repository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException("Contact introuvable: " + contactId));
        if (dto.getPatientId() != null && !dto.getPatientId().equals(contact.getPatientId())) {
            throw new IllegalArgumentException("Le contact appartient à un autre patient");
        }
        contact.setNom(dto.getNom());
        contact.setTelephone(dto.getTelephone());
        contact.setRelation(dto.getRelation());
        contact.setEmail(dto.getEmail());
        return toDto(repository.save(contact));
    }

    @Transactional
    public void delete(Long contactId) {
        if (!repository.existsById(contactId)) {
            throw new EntityNotFoundException("Contact introuvable: " + contactId);
        }
        repository.deleteById(contactId);
    }

    private ContactDto toDto(Contact contact) {
        return ContactDto.builder()
                .id(contact.getId())
                .patientId(contact.getPatientId())
                .nom(contact.getNom())
                .telephone(contact.getTelephone())
                .relation(contact.getRelation())
                .email(contact.getEmail())
                .build();
    }
}
