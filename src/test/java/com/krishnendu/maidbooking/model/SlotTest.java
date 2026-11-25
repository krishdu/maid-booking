package com.krishnendu.maidbooking.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SlotTest {

    private static final LocalDateTime DAY = LocalDateTime.of(2030, 1, 1, 0, 0);

    private Slot slot(int startHour, int endHour) {
        return new Slot(DAY.withHour(startHour), DAY.withHour(endHour));
    }

    @Test
    void overlappingSlotsAreDetected() {
        assertTrue(slot(9, 11).overlaps(slot(10, 12)));
        assertTrue(slot(10, 12).overlaps(slot(9, 11)));
        assertTrue(slot(9, 12).overlaps(slot(10, 11)));
    }

    @Test
    void backToBackSlotsDoNotOverlap() {
        assertFalse(slot(9, 10).overlaps(slot(10, 11)));
        assertFalse(slot(10, 11).overlaps(slot(9, 10)));
    }

    @Test
    void startMustBeBeforeEnd() {
        assertThrows(IllegalArgumentException.class, () -> slot(10, 10));
    }
}
