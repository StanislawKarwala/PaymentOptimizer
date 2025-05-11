package org.example.service;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.model.PaymentResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentOptimizerTest {
    private PaymentOptimizer optimizer;
    private List<Order> orders;
    private List<PaymentMethod> paymentMethods;

    @BeforeEach
    void setUp() {
        optimizer = new PaymentOptimizer();

        // Tworzymy przykładowe zamówienia
        orders = Arrays.asList(
                new Order("ORDER1", new BigDecimal("100.00"), Arrays.asList("mZysk")),
                new Order("ORDER2", new BigDecimal("200.00"), Arrays.asList("BosBankrut")),
                new Order("ORDER3", new BigDecimal("150.00"), Arrays.asList("mZysk", "BosBankrut")),
                new Order("ORDER4", new BigDecimal("50.00"), null)
        );

        // Tworzymy przykładowe metody płatności
        paymentMethods = Arrays.asList(
                new PaymentMethod("PUNKTY", 15, new BigDecimal("100.00")),
                new PaymentMethod("mZysk", 10, new BigDecimal("180.00")),
                new PaymentMethod("BosBankrut", 5, new BigDecimal("200.00"))
        );
    }

    @Test
    void testOptimizePayments() {
        System.out.println("\n=== Test optymalizacji płatności ===");
        PaymentResult result = optimizer.optimizePayments(orders, paymentMethods);

        System.out.println("\nWynik optymalizacji:");
        System.out.println(result);

        // Sprawdzamy czy wyniki są zgodne z oczekiwanymi
        assertEquals(new BigDecimal("165.00"), result.getPaymentAmounts().get("mZysk"));
        assertEquals(new BigDecimal("190.00"), result.getPaymentAmounts().get("BosBankrut"));
        assertEquals(new BigDecimal("100.00"), result.getPaymentAmounts().get("PUNKTY"));
    }

    @Test
    void testFindBestPaymentMethod() {
        System.out.println("\n=== Test znajdowania najlepszej metody płatności ===");

        // Test dla ORDER3 (150.00) z promocjami [mZysk, BosBankrut]
        Order order3 = orders.get(2);
        Set<String> usedPaymentMethods = new HashSet<>();
        PaymentMethod bestMethod = optimizer.findBestPaymentMethod(
                order3.getValue(),
                paymentMethods,
                order3.getPromotions(),
                usedPaymentMethods
        );

        System.out.println("\nTest dla ORDER3 (150.00):");
        System.out.println("Dostępne promocje: " + order3.getPromotions());
        System.out.println("Znaleziona najlepsza metoda: " + bestMethod.getId());
        System.out.println("Rabat: " + bestMethod.getDiscount() + "%");

        assertEquals("mZysk", bestMethod.getId());
        assertEquals(10, bestMethod.getDiscount());
    }

    @Test
    void testApplyDiscount() {
        System.out.println("\n=== Test obliczania rabatu ===");

        BigDecimal amount = new BigDecimal("100.00");
        int discount = 10;

        BigDecimal result = optimizer.applyDiscount(amount, discount);

        System.out.println("\nTest obliczania rabatu:");
        System.out.println("Kwota przed rabatem: " + amount);
        System.out.println("Rabat: " + discount + "%");
        System.out.println("Kwota po rabacie: " + result);

        assertEquals(new BigDecimal("90.00"), result);
    }

    @Test
    void testPartialPointsPayment() {
        System.out.println("\n=== Test częściowej płatności punktami ===");

        Order order4 = orders.get(3);
        PaymentMethod pointsMethod = paymentMethods.get(0);

        System.out.println("\nTest dla ORDER4 (50.00):");
        System.out.println("Kwota zamówienia: " + order4.getValue());
        System.out.println("Limit punktów: " + pointsMethod.getLimit());

        BigDecimal pointsAmount = order4.getValue().multiply(new BigDecimal("0.10"))
                .setScale(2, java.math.RoundingMode.HALF_UP);
        BigDecimal discountedTotal = optimizer.applyDiscount(order4.getValue(), 10);
        BigDecimal remainingAfterPoints = discountedTotal.subtract(pointsAmount)
                .setScale(2, java.math.RoundingMode.HALF_UP);

        System.out.println("Kwota punktów (10%): " + pointsAmount);
        System.out.println("Kwota po rabacie 10%: " + discountedTotal);
        System.out.println("Pozostała kwota: " + remainingAfterPoints);

        assertTrue(pointsAmount.compareTo(new BigDecimal("5.00")) >= 0);
        assertEquals(new BigDecimal("45.00"), discountedTotal);
        assertEquals(new BigDecimal("40.00"), remainingAfterPoints);
    }

    @Test
    void testBasicFullPayment() {
        // Test podstawowej pełnej płatności jedną metodą
        List<Order> singleOrder = Arrays.asList(
                new Order("ORDER5", new BigDecimal("100.00"), Arrays.asList("BosBankrut"))
        );

        List<PaymentMethod> methods = Arrays.asList(
                new PaymentMethod("BosBankrut", 5, new BigDecimal("200.00"))
        );

        PaymentResult result = optimizer.optimizePayments(singleOrder, methods);
        assertEquals(new BigDecimal("95.00"), result.getPaymentAmounts().get("BosBankrut"));
    }

    @Test
    void testBasicPointsPayment() {
        // Test podstawowej płatności punktami
        List<Order> singleOrder = Arrays.asList(
                new Order("ORDER6", new BigDecimal("50.00"), null)
        );

        List<PaymentMethod> methods = Arrays.asList(
                new PaymentMethod("PUNKTY", 15, new BigDecimal("100.00"))
        );

        PaymentResult result = optimizer.optimizePayments(singleOrder, methods);
        assertEquals(new BigDecimal("50.00"), result.getPaymentAmounts().get("PUNKTY"));
    }

    @Test
    void testBasicDiscount() {
        // Test podstawowego rabatu
        BigDecimal amount = new BigDecimal("200.00");
        int discount = 5;
        BigDecimal result = optimizer.applyDiscount(amount, discount);
        assertEquals(new BigDecimal("190.00"), result);
    }

    @Test
    void testBasicPaymentMethodSelection() {
        // Test podstawowego wyboru metody płatności
        Order order = new Order("ORDER7", new BigDecimal("100.00"), Arrays.asList("mZysk", "BosBankrut"));
        Set<String> usedMethods = new HashSet<>();

        PaymentMethod method = optimizer.findBestPaymentMethod(
                order.getValue(),
                paymentMethods,
                order.getPromotions(),
                usedMethods
        );

        assertEquals("mZysk", method.getId());
        assertEquals(10, method.getDiscount());
    }

    @Test
    void testBasicPointsPartialPayment() {
        // Test podstawowej częściowej płatności punktami
        List<Order> singleOrder = Arrays.asList(
                new Order("ORDER8", new BigDecimal("100.00"), null)
        );

        List<PaymentMethod> methods = Arrays.asList(
                new PaymentMethod("PUNKTY", 15, new BigDecimal("20.00")),
                new PaymentMethod("mZysk", 10, new BigDecimal("100.00"))
        );

        PaymentResult result = optimizer.optimizePayments(singleOrder, methods);
        assertTrue(result.getPaymentAmounts().containsKey("PUNKTY"));
        assertTrue(result.getPaymentAmounts().containsKey("mZysk"));
    }
}