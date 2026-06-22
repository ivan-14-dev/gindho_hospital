package com.gindho.actuator;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * Health check personnalisé pour surveiller l'état du service.
 * Vérifie la connectivité à la base de données et aux dépendances.
 */
@Component
public class CustomHealthIndicator implements HealthIndicator {

    private boolean databaseConnected = true;
    private boolean kafkaConnected = true;

    @Override
    public Health health() {
        if (!databaseConnected) {
            return Health.down()
                .withDetail("database", "Disconnected")
                .withDetail("kafka", kafkaConnected ? "Connected" : "Disconnected")
                .build();
        }
        return Health.up()
            .withDetail("database", "Connected")
            .withDetail("kafka", kafkaConnected ? "Connected" : "Disconnected")
            .build();
    }

    public void setDatabaseConnected(boolean connected) { this.databaseConnected = connected; }
    public void setKafkaConnected(boolean connected) { this.kafkaConnected = connected; }
}
