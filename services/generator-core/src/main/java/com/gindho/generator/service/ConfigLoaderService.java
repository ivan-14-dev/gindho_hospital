package com.gindho.generator.service;
import com.gindho.generator.config.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor; import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource; import org.springframework.stereotype.Service;
import java.io.File; import java.io.InputStream; import java.util.*;
@Service @Slf4j @RequiredArgsConstructor
public class ConfigLoaderService {
    private final ObjectMapper objectMapper;
    public UILayoutConfig loadConfig(String appName) {
        try {
            String path = "ui-config/" + appName + ".json";
            InputStream is = new ClassPathResource(path).getInputStream();
            return objectMapper.readValue(is, UILayoutConfig.class);
        } catch (Exception e) {
            log.warn("Config not found for {}: {}, using defaults", appName, e.getMessage());
            return UILayoutConfig.builder().appName(appName).version("1.0")
                .sections(List.of()).menus(List.of()).build();
        }
    }
    public List<String> getAllConfigs() {
        return List.of("hospital", "patient", "admin");
    }
}