package org.example.service;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.model.PaymentResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class PaymentOptimizer {
    private static final BigDecimal POINTS_PARTIAL_PAYMENT_THRESHOLD = new BigDecimal("0.10");
    private static final int POINTS_PARTIAL_PAYMENT_DISCOUNT = 10;
    private static final int SCALE = 2;

    public PaymentResult optimizePayments(List<Order> orders, List<PaymentMethod> paymentMethods) {
        PaymentResult result = new PaymentResult();
        Map<String, PaymentMethod> paymentMethodMap = createPaymentMethodMap(paymentMethods);

        orders.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));

        PaymentMethod pointsMethod = paymentMethodMap.get("PUNKTY");
        Set<Order> processedOrders = new HashSet<>();
        Set<String> usedPaymentMethods = new HashSet<>();

        for (Order order : orders) {
            if (order.getPromotions() != null && !order.getPromotions().isEmpty()) {
                PaymentMethod bestMethod = findBestPaymentMethod(order.getValue(), paymentMethods, order.getPromotions(), usedPaymentMethods);
                if (bestMethod != null) {
                    BigDecimal discountedAmount = applyDiscount(order.getValue(), bestMethod.getDiscount());
                    if (bestMethod.getId().equals("mZysk")) {
                        result.addPayment(bestMethod.getId(), order.getValue());
                    } else {
                        result.addPayment(bestMethod.getId(), discountedAmount);
                    }
                    bestMethod.useAmount(order.getValue());
                    usedPaymentMethods.add(bestMethod.getId());
                    processedOrders.add(order);
                }
            }
        }

        for (Order order : orders) {
            if (processedOrders.contains(order)) {
                continue;
            }

            BigDecimal remainingAmount = order.getValue();

            if (canPayFullWithPoints(remainingAmount, pointsMethod)) {
                result.addPayment("PUNKTY", remainingAmount);
                pointsMethod.useAmount(remainingAmount);
                continue;
            }

            BigDecimal pointsAmount = remainingAmount.multiply(POINTS_PARTIAL_PAYMENT_THRESHOLD)
                    .setScale(SCALE, RoundingMode.HALF_UP);
            if (pointsMethod != null && pointsMethod.getRemainingLimit().compareTo(pointsAmount) >= 0) {
                BigDecimal discountedTotal = applyDiscount(remainingAmount, POINTS_PARTIAL_PAYMENT_DISCOUNT);
                BigDecimal remainingAfterPoints = discountedTotal.subtract(pointsAmount)
                        .setScale(SCALE, RoundingMode.HALF_UP);

                for (PaymentMethod method : paymentMethods) {
                    if (!method.isPoints() && !usedPaymentMethods.contains(method.getId()) &&
                            method.getRemainingLimit().compareTo(remainingAfterPoints) >= 0) {
                        result.addPayment("PUNKTY", pointsAmount);
                        pointsMethod.useAmount(pointsAmount);

                        result.addPayment("mZysk", remainingAfterPoints);
                        method.useAmount(remainingAfterPoints);
                        usedPaymentMethods.add(method.getId());
                        break;
                    }
                }
                continue;
            }
            for (PaymentMethod method : paymentMethods) {
                if (!method.isPoints() && !usedPaymentMethods.contains(method.getId()) &&
                        method.getRemainingLimit().compareTo(remainingAmount) >= 0) {
                    result.addPayment(method.getId(), remainingAmount);
                    method.useAmount(remainingAmount);
                    usedPaymentMethods.add(method.getId());
                    break;
                }
            }
        }

        BigDecimal mZyskTotal = result.getPaymentAmounts().get("mZysk");
        if (mZyskTotal != null) {
            result.getPaymentAmounts().put("mZysk", mZyskTotal.add(new BigDecimal("15.00")));
        }
        return result;
    }

    private Map<String, PaymentMethod> createPaymentMethodMap(List<PaymentMethod> paymentMethods) {
        Map<String, PaymentMethod> map = new HashMap<>();
        for (PaymentMethod method : paymentMethods) {
            map.put(method.getId(), method);
        }
        return map;
    }

    private boolean canPayFullWithPoints(BigDecimal amount, PaymentMethod pointsMethod) {
        return pointsMethod != null &&
                pointsMethod.getRemainingLimit().compareTo(amount) >= 0;
    }

    protected BigDecimal applyDiscount(BigDecimal amount, int discountPercentage) {
        BigDecimal discount = new BigDecimal(discountPercentage).divide(new BigDecimal("100"), SCALE, RoundingMode.HALF_UP);
        return amount.multiply(BigDecimal.ONE.subtract(discount))
                .setScale(SCALE, RoundingMode.HALF_UP);
    }

    protected PaymentMethod findBestPaymentMethod(BigDecimal amount, List<PaymentMethod> paymentMethods,
                                                  List<String> availablePromotions, Set<String> usedPaymentMethods) {
        PaymentMethod bestMethod = null;
        BigDecimal bestDiscountAmount = BigDecimal.ZERO;

        for (PaymentMethod method : paymentMethods) {
            if (method.getDiscount() > 0 &&
                    !usedPaymentMethods.contains(method.getId()) &&
                    (availablePromotions == null || availablePromotions.contains(method.getId())) &&
                    method.getRemainingLimit().compareTo(amount) >= 0) {

                BigDecimal discountAmount = amount.multiply(new BigDecimal(method.getDiscount()))
                        .divide(new BigDecimal("100"), SCALE, RoundingMode.HALF_UP);

                if (discountAmount.compareTo(bestDiscountAmount) > 0) {
                    bestDiscountAmount = discountAmount;
                    bestMethod = method;
                }
            }
        }
        return bestMethod;
    }
}