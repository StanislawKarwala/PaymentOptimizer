package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Order;
import org.example.model.PaymentMethod;

import java.io.File;
import java.util.List;

public class JsonReader {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static List<Order> readOrders(String filePath) throws Exception {
        return mapper.readValue(
                new File(filePath),
                new TypeReference<List<Order>>(){}
        );
    }

    public static List<PaymentMethod> readPaymentMethods(String filePath) throws Exception {
        return mapper.readValue(
                new File(filePath),
                new TypeReference<List<PaymentMethod>>(){}
        );
    }
}