package com.krishnendu.maidbooking.service;

import com.krishnendu.maidbooking.exception.BookingException;
import com.krishnendu.maidbooking.model.Booking;
import com.krishnendu.maidbooking.model.BookingType;
import com.krishnendu.maidbooking.model.Maid;
import com.krishnendu.maidbooking.model.Slot;
import com.krishnendu.maidbooking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.NoSuchElementException;

@Service
public class BookingService {

    private final MaidService maidService;
    private final BookingRepository bookingRepository;

    public BookingService(MaidService maidService, BookingRepository bookingRepository) {
        this.maidService = maidService;
        this.bookingRepository = bookingRepository;
    }

    public synchronized Booking book(String maidId, BookingType type, LocalDateTime requestedStart, int hours) {
        Maid maid = maidService.get(maidId);
        LocalDateTime start = type.resolveStart(requestedStart, LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES));
        Slot slot = new Slot(start, start.plusHours(hours));
        ensureMaidWorks(maid, slot);
        ensureSlotFree(maidId, slot);
        return bookingRepository.save(new Booking(maidId, slot, type, maid.priceFor(slot)));
    }

    public synchronized Booking cancel(String bookingId) {
        Booking booking = get(bookingId);
        booking.cancel();
        return bookingRepository.save(booking);
    }

    public Booking get(String bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NoSuchElementException("Booking not found: " + bookingId));
    }

    private void ensureMaidWorks(Maid maid, Slot slot) {
        if (!maid.worksDuring(slot)) {
            throw new BookingException("Maid does not work during the requested slot");
        }
    }

    private void ensureSlotFree(String maidId, Slot slot) {
        boolean taken = bookingRepository.findByMaidId(maidId).stream().anyMatch(b -> b.blocks(slot));
        if (taken) {
            throw new BookingException("Maid is already booked for the requested slot");
        }
    }
}
