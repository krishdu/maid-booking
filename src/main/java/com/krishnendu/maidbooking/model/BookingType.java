package com.krishnendu.maidbooking.model;

import java.time.LocalDateTime;

public enum BookingType {

    INSTANT {
        @Override
        public LocalDateTime resolveStart(LocalDateTime requestedStart, LocalDateTime now) {
            return now;
        }
    },

    SCHEDULED {
        @Override
        public LocalDateTime resolveStart(LocalDateTime requestedStart, LocalDateTime now) {
            if (requestedStart == null || !requestedStart.isAfter(now)) {
                throw new IllegalArgumentException("Scheduled booking needs a future start time");
            }
            return requestedStart;
        }
    };

    public abstract LocalDateTime resolveStart(LocalDateTime requestedStart, LocalDateTime now);
}
