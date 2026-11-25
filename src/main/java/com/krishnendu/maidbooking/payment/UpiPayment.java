package com.krishnendu.maidbooking.payment;

import org.springframework.stereotype.Component;

@Component
public class UpiPayment implements PaymentProcessor {

    @Override
    public String method() {
        return "UPI";
    }

    @Override
    public boolean pay(double amount, String upiId) {
        return upiId != null && upiId.matches("[\\w.-]+@[\\w]+");
    }
}
