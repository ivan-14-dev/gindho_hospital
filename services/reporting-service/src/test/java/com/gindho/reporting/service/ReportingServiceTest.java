package com.gindho.reporting.service;

import com.gindho.reporting.repository.RapportRepository;
import com.gindho.kafka.EventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReportingServiceTest {
    @Mock private RapportRepository repository;
    @Mock private EventProducer eventProducer;
    @InjectMocks private ReportingService service;

    @Test void contextLoads() { assertNotNull(service); }
}
