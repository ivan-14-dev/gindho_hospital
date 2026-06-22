package com.gindho.laboratory.service;

import com.gindho.laboratory.repository.AnalyseRepository;
import com.gindho.laboratory.repository.LaboratoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LaboratoryService {
    private final LaboratoryRepository laboratoryRepository;
    private final AnalyseRepository analyseRepository;
}
