package com.gindho.authorization.service;

import com.gindho.authorization.repository.AuthorizationUserRepository;
import com.gindho.kafka.EventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceTest {
    @Mock private AuthorizationUserRepository repository;
    @Mock private EventProducer eventProducer;
    @InjectMocks private AuthorizationService service;

    @Test void contextLoads() { assertNotNull(service); }
}
