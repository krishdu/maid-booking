package com.krishnendu.maidbooking.model;

import com.krishnendu.maidbooking.exception.BookingException;

import java.util.UUID;

public class Booking {

    private final String id;
    private final String maidId;
    private final Slot slot;
    private final BookingType type;
    private final double amount;
    private BookingStatus status;

    public Booking(String maidId, Slot slot, BookingType type, double amount) {
        this.id = UUID.randomUUID().toString();
        this.maidId = maidId;
        this.slot = slot;
        this.type = type;
        this.amount = amount;
        this.status = BookingStatus.CREATED;
    }

    public void ensurePayable() {
        if (status != BookingStatus.CREATED) {
            throw new BookingException("Booking cannot be paid in status " + status);
        }
    }

    public void markPaid() {
        ensurePayable();
        status = BookingStatus.PAID;
    }

    public void cancel() {
        if (status == BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled");
        }
        status = BookingStatus.CANCELLED;
    }

    public boolean blocks(Slot other) {
        return status != BookingStatus.CANCELLED && slot.overlaps(other);
    }

    public String getId() {
        return id;
    }

    public String getMaidId() {
        return maidId;
    }

    public Slot getSlot() {
        return slot;
    }

    public BookingType getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public BookingStatus getStatus() {
        return status;
    }
}
