package com.gindho.setup.service;

import com.gindho.setup.dto.SetupRequest;

import java.util.Map;

public interface SetupService {

    Map<String, Object> getSetupStatus();

    Map<String, Object> startSetup(SetupRequest request);

    Map<String, Object> getProgress();

    Map<String, Object> resetSetup();
}