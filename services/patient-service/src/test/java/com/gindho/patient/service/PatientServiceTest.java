package com.gindho.patient.service;

import com.gindho.patient.dto.PatientDto;
import com.gindho.patient.model.Patient;
import com.gindho.patient.repository.PatientRepository;
import com.gindho.kafka.EventProducer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock private PatientRepository patientRepository;
    @Mock private EventProducer eventProducer;
    @Mock private ObjectMapper objectMapper;
    @InjectMocks private PatientService patientService;

    @Test
    void findByNumeroPatient_ExistingNumber_ReturnsPatient() {
        Patient patient = new Patient();
        patient.setNumeroPatient("PAT-2026-0001");
        when(patientRepository.findByNumeroPatientAndActifTrue("PAT-2026-0001")).thenReturn(Optional.of(patient));
        
        Optional<Patient> result = patientService.findByNumeroPatient("PAT-2026-0001");
        assertTrue(result.isPresent());
        assertEquals("PAT-2026-0001", result.get().getNumeroPatient());
    }

    @Test
    void findByNumeroPatient_NonExisting_ReturnsEmpty() {
        when(patientRepository.findByNumeroPatientAndActifTrue("INVALID")).thenReturn(Optional.empty());
        Optional<Patient> result = patientService.findByNumeroPatient("INVALID");
        assertTrue(result.isEmpty());
    }

    @Test
    void createPatient_GeneratesPatientNumber() {
        PatientDto dto = PatientDto.builder()
                .sexe("HOMME")
                .build();
                
        when(patientRepository.save(any(Patient.class))).thenAnswer(invocation -> {
            Patient p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });
        
        PatientDto result = patientService.create(dto);
        assertNotNull(result);
        assertNotNull(result.getNumeroPatient());
        assertTrue(result.getNumeroPatient().startsWith("PAT-"));
    }
}