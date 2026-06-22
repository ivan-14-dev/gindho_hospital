package com.gindho.medical-record.service;

import com.gindho.medical-record.repository.Medical-recordRepository;
import com.gindho.kafka.EventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class Medical-recordServiceTest {
    @Mock private Medical-recordRepository repository;
    @Mock private EventProducer eventProducer;
    @InjectMocks private Medical-recordService service;

    @Test void contextLoads() { assertNotNull(service); }
}
