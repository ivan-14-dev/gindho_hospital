package com.gindho.laboratory.service;

import com.gindho.laboratory.model.Analyse;
import com.gindho.laboratory.repository.AnalyseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LaboratoryServiceTest {
    @Mock private AnalyseRepository repository;
    @InjectMocks private AnalyseService service;

    @Test void contextLoads() { assertNotNull(service); }
}
