package com.gindho.pharmacy.service;

import com.gindho.pharmacy.repository.PharmacyRepository;
import com.gindho.kafka.EventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PharmacyServiceTest {
    @Mock private PharmacyRepository repository;
    @Mock private EventProducer eventProducer;
    @InjectMocks private PharmacyService service;

    @Test void contextLoads() { assertNotNull(service); }
}
