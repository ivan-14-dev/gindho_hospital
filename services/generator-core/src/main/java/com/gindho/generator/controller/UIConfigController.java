package com.gindho.generator.controller;
import com.gindho.generator.config.*; import com.gindho.generator.service.*;
import com.gindho.base.ApiResponse; import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity; import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/ui") @RequiredArgsConstructor
public class UIConfigController {
    private final ConfigLoaderService configLoader;
    private final PermissionFilterService permissionFilter;
    @GetMapping("/config/{appName}")
    public ResponseEntity<ApiResponse<UILayoutConfig>> getConfig(@PathVariable String appName) {
        UILayoutConfig config = configLoader.loadConfig(appName);
        // TODO: filter by user permissions from JWT
        return ResponseEntity.ok(ApiResponse.of(config));
    }
    @GetMapping("/config/{appName}/sections")
    public ResponseEntity<ApiResponse<List<UISectionDefinition>>> getSections(@PathVariable String appName) {
        UILayoutConfig config = configLoader.loadConfig(appName);
        return ResponseEntity.ok(ApiResponse.of(config.getSections()));
    }
}
