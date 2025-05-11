package org.example.model;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class PaymentResult {
    private Map<String, BigDecimal> paymentAmounts;

    public PaymentResult() {
        this.paymentAmounts = new HashMap<>();
    }

    public void addPayment(String methodId, BigDecimal amount) {
        paymentAmounts.merge(methodId, amount, BigDecimal::add);
    }

    public Map<String, BigDecimal> getPaymentAmounts() {
        return paymentAmounts;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        paymentAmounts.forEach((methodId, amount) ->
                result.append(methodId).append(" ").append(amount).append("\n")
        );
        return result.toString();
    }
}