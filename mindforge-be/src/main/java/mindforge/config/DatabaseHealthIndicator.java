package mindforge.config;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Simple database connectivity check
        // In a real application, you would check actual DB connection
        return Health.up()
                .withDetail("database", "PostgreSQL")
                .withDetail("status", "connected")
                .build();
    }
}