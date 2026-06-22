package com.gindho.prescription.service;

import com.gindho.prescription.repository.OrdonnanceRepository;
import com.gindho.kafka.EventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PrescriptionServiceTest {
    @Mock private OrdonnanceRepository repository;
    @Mock private EventProducer eventProducer;
    @InjectMocks private PrescriptionService service;

    @Test void contextLoads() { assertNotNull(service); }
}