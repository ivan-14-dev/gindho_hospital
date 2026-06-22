package com.gindho.billing.service;

import com.gindho.billing.repository.BillingRepository;
import com.gindho.billing.repository.FactureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BillingService {
    private final BillingRepository billingRepository;
    private final FactureRepository factureRepository;
}
