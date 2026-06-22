package com.gindho.hr.service;

import com.gindho.hr.repository.EmployeRepository;
import com.gindho.hr.repository.CongeRepository;
import com.gindho.hr.repository.PresenceRepository;
import com.gindho.kafka.EventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HrServiceTest {
    @Mock private EmployeRepository employeRepository;
    @Mock private CongeRepository congeRepository;
    @Mock private PresenceRepository presenceRepository;
    @Mock private EventProducer eventProducer;
    @InjectMocks private HrService service;

    @Test void contextLoads() { assertNotNull(service); }
}
