package com.krishnendu.maidbooking.controller;

import com.krishnendu.maidbooking.dto.BookingRequest;
import com.krishnendu.maidbooking.dto.PaymentRequest;
import com.krishnendu.maidbooking.model.Booking;
import com.krishnendu.maidbooking.service.BookingService;
import com.krishnendu.maidbooking.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final PaymentService paymentService;

    public BookingController(BookingService bookingService, PaymentService paymentService) {
        this.bookingService = bookingService;
        this.paymentService = paymentService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking book(@Valid @RequestBody BookingRequest request) {
        return bookingService.book(request.maidId(), request.type(), request.start(), request.hours());
    }

    @GetMapping("/{id}")
    public Booking get(@PathVariable String id) {
        return bookingService.get(id);
    }

    @PostMapping("/{id}/payment")
    public Booking pay(@PathVariable String id, @Valid @RequestBody PaymentRequest request) {
        return paymentService.pay(id, request.method(), request.details());
    }

    @PostMapping("/{id}/cancel")
    public Booking cancel(@PathVariable String id) {
        return bookingService.cancel(id);
    }
}
