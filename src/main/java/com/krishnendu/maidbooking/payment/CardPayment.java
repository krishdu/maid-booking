package com.krishnendu.maidbooking.payment;

import org.springframework.stereotype.Component;

@Component
public class CardPayment implements PaymentProcessor {

    @Override
    public String method() {
        return "CARD";
    }

    @Override
    public boolean pay(double amount, String cardNumber) {
        return cardNumber != null && cardNumber.matches("\\d{16}");
    }
}
