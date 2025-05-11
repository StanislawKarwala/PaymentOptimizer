package org.example;

import org.example.model.Order;
import org.example.model.PaymentMethod;
import org.example.model.PaymentResult;
import org.example.service.JsonReader;
import org.example.service.PaymentOptimizer;

import java.io.IOException;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Użycie: java -jar app.jar <ścieżka_do_orders.json> <ścieżka_do_paymentmethods.json>");
            System.exit(1);
        }

        try {
            List<Order> orders = JsonReader.readOrders(args[0]);
            List<PaymentMethod> paymentMethods = JsonReader.readPaymentMethods(args[1]);

            PaymentOptimizer optimizer = new PaymentOptimizer();
            PaymentResult result = optimizer.optimizePayments(orders, paymentMethods);

            System.out.print(result);
        } catch (IOException e) {
            System.err.println("Błąd podczas odczytu plików: " + e.getMessage());
            System.exit(1);
        } catch (Exception e) {
            System.err.println("Wystąpił nieoczekiwany błąd: " + e.getMessage());
            System.exit(1);
        }
    }
}