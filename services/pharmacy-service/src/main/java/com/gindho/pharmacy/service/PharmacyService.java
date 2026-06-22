package com.gindho.pharmacy.service;

import com.gindho.pharmacy.repository.MedicamentRepository;
import com.gindho.pharmacy.repository.PharmacyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PharmacyService {
    private final PharmacyRepository pharmacyRepository;
    private final MedicamentRepository medicamentRepository;
}
