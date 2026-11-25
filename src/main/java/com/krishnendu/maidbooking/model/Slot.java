package com.krishnendu.maidbooking.model;

import java.time.Duration;
import java.time.LocalDateTime;

public record Slot(LocalDateTime start, LocalDateTime end) {

    public Slot {
        if (start == null || end == null || !start.isBefore(end)) {
            throw new IllegalArgumentException("Slot start must be before end");
        }
    }

    public boolean overlaps(Slot other) {
        return start.isBefore(other.end) && other.start.isBefore(end);
    }

    public double hours() {
        return Duration.between(start, end).toMinutes() / 60.0;
    }
}
