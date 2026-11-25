package com.krishnendu.maidbooking.service;

import com.krishnendu.maidbooking.exception.BookingException;
import com.krishnendu.maidbooking.model.Booking;
import com.krishnendu.maidbooking.payment.PaymentProcessor;
import com.krishnendu.maidbooking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PaymentService {

    private final Map<String, PaymentProcessor> processors;
    private final BookingService bookingService;
    private final BookingRepository bookingRepository;

    public PaymentService(List<PaymentProcessor> processors, BookingService bookingService,
                          BookingRepository bookingRepository) {
        this.processors = processors.stream()
                .collect(Collectors.toMap(PaymentProcessor::method, Function.identity()));
        this.bookingService = bookingService;
        this.bookingRepository = bookingRepository;
    }

    public Booking pay(String bookingId, String method, String details) {
        Booking booking = bookingService.get(bookingId);
        booking.ensurePayable();
        if (!processorFor(method).pay(booking.getAmount(), details)) {
            throw new BookingException("Payment declined");
        }
        booking.markPaid();
        return bookingRepository.save(booking);
    }

    private PaymentProcessor processorFor(String method) {
        return Optional.ofNullable(processors.get(method.toUpperCase()))
                .orElseThrow(() -> new IllegalArgumentException("Unsupported payment method: " + method));
    }
}
