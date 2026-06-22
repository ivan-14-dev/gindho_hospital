package com.gindho.pharmacy.service;

import com.gindho.pharmacy.model.Medicament;
import com.gindho.pharmacy.repository.MedicamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PharmacyServiceTest {
    @Mock private MedicamentRepository repository;
    @InjectMocks private Medicament service;

    @Test void contextLoads() { assertNotNull(service); }
}
