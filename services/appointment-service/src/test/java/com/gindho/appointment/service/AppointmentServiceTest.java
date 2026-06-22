package com.gindho.appointment.service;

import com.gindho.appointment.repository.RendezVousRepository;
import com.gindho.kafka.EventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {
    @Mock private RendezVousRepository repository;
    @Mock private EventProducer eventProducer;
    @InjectMocks private RendezVousService service;

    @Test void contextLoads() { assertNotNull(service); }
}