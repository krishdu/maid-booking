package com.krishnendu.maidbooking.payment;

public interface PaymentProcessor {

    String method();

    boolean pay(double amount, String details);
}
