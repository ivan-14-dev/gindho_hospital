package com.gindho.controller;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/actuator")
public class GatewayHealthController {

    private final RouteLocator routeLocator;

    public GatewayHealthController(RouteLocator routeLocator) {
        this.routeLocator = routeLocator;
    }

    @GetMapping("/health")
    public Mono<ResponseEntity<Map<String, Object>>> health() {
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "UP");
        Map<String, String> services = new LinkedHashMap<>();
        services.put("identity-service", "UP");
        services.put("patient-service", "UP");
        services.put("appointment-service", "UP");
        services.put("medical-record-service", "UP");
        services.put("admission-service", "UP");
        services.put("emergency-service", "UP");
        services.put("ward-service", "UP");
        services.put("bed-service", "UP");
        services.put("round-service", "UP");
        services.put("surgery-service", "UP");
        services.put("prescription-service", "UP");
        services.put("pharmacy-service", "UP");
        services.put("laboratory-service", "UP");
        services.put("imaging-service", "UP");
        services.put("billing-service", "UP");
        services.put("insurance-service", "UP");
        services.put("payment-service", "UP");
        services.put("inventory-service", "UP");
        services.put("procurement-service", "UP");
        services.put("asset-service", "UP");
        services.put("ambulance-service", "UP");
        services.put("hr-service", "UP");
        services.put("scheduling-service", "UP");
        services.put("event-service", "UP");
        services.put("notification-service", "UP");
        services.put("reporting-service", "UP");
        services.put("audit-service", "UP");
        services.put("authorization-service", "UP");
        services.put("setup-service", "UP");
        response.put("services", services);
        return Mono.just(ResponseEntity.ok(response));
    }

    @GetMapping("/routes")
    public Mono<ResponseEntity<List<String>>> routes() {
        List<String> routeIds = routeLocator.getRoutes()
                .map(route -> route.getId())
                .collectList()
                .block();
        return Mono.just(ResponseEntity.ok(routeIds));
    }
}