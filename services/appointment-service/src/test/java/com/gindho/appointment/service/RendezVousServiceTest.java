package com.gindho.appointment.service;

import com.gindho.appointment.dto.RendezVousDto;
import com.gindho.appointment.model.RendezVous;
import com.gindho.appointment.repository.DisponibiliteRepository;
import com.gindho.appointment.repository.RendezVousRepository;
import com.gindho.kafka.EventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RendezVousServiceTest {

    @Mock private RendezVousRepository rendezVousRepository;
    @Mock private DisponibiliteRepository disponibiliteRepository;
    @Mock private EventProducer eventProducer;
    @InjectMocks private RendezVousService rendezVousService;

    @Test
    void findById_Existing_ReturnsDto() {
        RendezVous rdv = new RendezVous();
        rdv.setId(1L);
        rdv.setPatientId(1L);
        rdv.setMotif("Consultation");
        
        when(rendezVousRepository.findById(1L)).thenReturn(Optional.of(rdv));
        
        Optional<RendezVousDto> result = rendezVousService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals("Consultation", result.get().getMotif());
    }

    @Test
    void createAppointment_NoAvailability_ThrowsException() {
        RendezVousDto dto = new RendezVousDto();
        dto.setPatientId(1L);
        dto.setMedecinId(1L);
        dto.setDateHeureDebut(LocalDateTime.now().plusDays(1));
        dto.setDateHeureFin(LocalDateTime.now().plusDays(1).plusMinutes(30));
        
        when(disponibiliteRepository.findByMedecinIdAndDateAndDisponibleTrue(eq(1L), any(LocalDate.class)))
                .thenReturn(Collections.emptyList());
                
        assertThrows(IllegalStateException.class, () -> rendezVousService.create(dto));
    }
}