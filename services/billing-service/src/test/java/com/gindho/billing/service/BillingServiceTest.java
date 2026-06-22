package com.gindho.billing.service;

import com.gindho.billing.repository.BillingRepository;
import com.gindho.kafka.EventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BillingServiceTest {
    @Mock private BillingRepository repository;
    @Mock private EventProducer eventProducer;
    @InjectMocks private BillingService service;

    @Test void contextLoads() { assertNotNull(service); }
}
