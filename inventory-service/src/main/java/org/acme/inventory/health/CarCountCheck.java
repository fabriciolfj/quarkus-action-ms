package org.acme.inventory.health;

import io.smallrye.health.api.Wellness;
import jakarta.inject.Inject;
import org.acme.inventory.repository.CardRepository;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;

@Wellness
public class CarCountCheck implements HealthCheck {

    @Inject
    private CardRepository cardRepository;

    @Override
    public HealthCheckResponse call() {
        long carsCount = cardRepository.count();
        boolean wellness = carsCount > 0;

        return HealthCheckResponse.builder()
                .name("car-count-check")
                .status(wellness)
                .build();
    }
}
