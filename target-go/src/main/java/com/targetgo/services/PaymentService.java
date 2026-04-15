package com.targetgo.services;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PaymentService {

    /**
     * Simulates payment processing.
     * In a real app, this is where you'd call Stripe, PayPal, etc.
     *
     * Returns a PaymentResult with success/failure and a confirmation number.
     */
    public PaymentResult processPayment(String cardNumber, BigDecimal amount) {

        // Simulate card validation
        if (cardNumber == null || cardNumber.replaceAll("\\s", "").length() < 13) {
            return new PaymentResult(false, null, "Invalid card number");
        }

        // Simulate decline for test card "4000000000000002"
        if (cardNumber.replaceAll("\\s", "").equals("4000000000000002")) {
            return new PaymentResult(false, null, "Card declined");
        }

        // Simulate ~90% success rate for everything else
        boolean success = Math.random() > 0.1;
        if (!success) {
            return new PaymentResult(false, null, "Payment processor declined the transaction");
        }

        String confirmationNumber = "TGO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return new PaymentResult(true, confirmationNumber, "Payment approved");
    }

    // ── Inner result class ────────────────────────────────────────────
    public static class PaymentResult {
        public final boolean success;
        public final String confirmationNumber;
        public final String message;

        public PaymentResult(boolean success, String confirmationNumber, String message) {
            this.success = success;
            this.confirmationNumber = confirmationNumber;
            this.message = message;
        }
    }
}
