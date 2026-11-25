package com.krishnendu.maidbooking.repository;

import com.krishnendu.maidbooking.model.Booking;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBookingRepository implements BookingRepository {

    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    @Override
    public Booking save(Booking booking) {
        bookings.put(booking.getId(), booking);
        return booking;
    }

    @Override
    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(bookings.get(id));
    }

    @Override
    public List<Booking> findByMaidId(String maidId) {
        return bookings.values().stream()
                .filter(b -> b.getMaidId().equals(maidId))
                .toList();
    }
}
