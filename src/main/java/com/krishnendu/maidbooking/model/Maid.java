package com.krishnendu.maidbooking.model;

import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record Maid(String id,
                   String name,
                   String locality,
                   Set<ServiceType> services,
                   double pricePerHour,
                   double rating,
                   LocalTime workStart,
                   LocalTime workEnd) {

    public Maid(String name, String locality, Set<ServiceType> services, double pricePerHour,
                double rating, LocalTime workStart, LocalTime workEnd) {
        this(UUID.randomUUID().toString(), name, locality, Set.copyOf(services), pricePerHour, rating, workStart, workEnd);
    }

    public Maid {
        if (!workStart.isBefore(workEnd)) {
            throw new IllegalArgumentException("Work start must be before work end");
        }
    }

    public boolean offers(ServiceType service) {
        return services.contains(service);
    }

    public boolean worksDuring(Slot slot) {
        return slot.start().toLocalDate().equals(slot.end().toLocalDate())
                && !slot.start().toLocalTime().isBefore(workStart)
                && !slot.end().toLocalTime().isAfter(workEnd);
    }

    public double priceFor(Slot slot) {
        return pricePerHour * slot.hours();
    }
}
