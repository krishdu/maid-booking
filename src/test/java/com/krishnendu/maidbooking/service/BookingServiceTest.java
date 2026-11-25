package com.krishnendu.maidbooking.service;

import com.krishnendu.maidbooking.exception.BookingException;
import com.krishnendu.maidbooking.model.Booking;
import com.krishnendu.maidbooking.model.BookingStatus;
import com.krishnendu.maidbooking.model.BookingType;
import com.krishnendu.maidbooking.model.Maid;
import com.krishnendu.maidbooking.model.ServiceType;
import com.krishnendu.maidbooking.payment.CardPayment;
import com.krishnendu.maidbooking.payment.UpiPayment;
import com.krishnendu.maidbooking.repository.BookingRepository;
import com.krishnendu.maidbooking.repository.InMemoryBookingRepository;
import com.krishnendu.maidbooking.repository.InMemoryMaidRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingServiceTest {

    private static final LocalDateTime TOMORROW_10AM = LocalDate.now().plusDays(1).atTime(10, 0);

    private BookingService bookingService;
    private PaymentService paymentService;
    private String maidId;

    @BeforeEach
    void setUp() {
        MaidService maidService = new MaidService(new InMemoryMaidRepository());
        BookingRepository bookingRepository = new InMemoryBookingRepository();
        bookingService = new BookingService(maidService, bookingRepository);
        paymentService = new PaymentService(List.of(new UpiPayment(), new CardPayment()), bookingService, bookingRepository);
        maidId = maidService.add(new Maid("Asha", "Koramangala", Set.of(ServiceType.CLEANING), 200, 4.5,
                LocalTime.of(8, 0), LocalTime.of(18, 0))).id();
    }

    private Booking book(LocalDateTime start, int hours) {
        return bookingService.book(maidId, BookingType.SCHEDULED, start, hours);
    }

    @Test
    void bookingCalculatesAmountFromHourlyPrice() {
        Booking booking = book(TOMORROW_10AM, 2);

        assertEquals(400, booking.getAmount());
        assertEquals(BookingStatus.CREATED, booking.getStatus());
    }

    @Test
    void bookingAnAlreadyTakenSlotFails() {
        book(TOMORROW_10AM, 2);

        assertThrows(BookingException.class, () -> book(TOMORROW_10AM.plusHours(1), 2));
    }

    @Test
    void bookingOutsideWorkingHoursFails() {
        assertThrows(BookingException.class, () -> book(TOMORROW_10AM.withHour(17), 2));
    }

    @Test
    void cancelledBookingFreesTheSlot() {
        Booking first = book(TOMORROW_10AM, 2);
        bookingService.cancel(first.getId());

        Booking second = book(TOMORROW_10AM, 2);

        assertEquals(BookingStatus.CREATED, second.getStatus());
    }

    @Test
    void payingForCancelledBookingFails() {
        Booking booking = book(TOMORROW_10AM, 2);
        bookingService.cancel(booking.getId());

        assertThrows(BookingException.class,
                () -> paymentService.pay(booking.getId(), "UPI", "asha@upi"));
    }

    @Test
    void payingMarksBookingPaid() {
        Booking booking = book(TOMORROW_10AM, 2);

        Booking paid = paymentService.pay(booking.getId(), "card", "4111111111111111");

        assertEquals(BookingStatus.PAID, paid.getStatus());
    }
}
