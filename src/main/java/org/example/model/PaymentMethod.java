package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class PaymentMethod {
    @JsonProperty("id")
    private String id;

    @JsonProperty("discount")
    private int discount;

    @JsonProperty("limit")
    private BigDecimal limit;

    private BigDecimal remainingLimit;

    public PaymentMethod() {
    }

    public PaymentMethod(String id, int discount, BigDecimal limit) {
        this.id = id;
        this.discount = discount;
        this.limit = limit;
        this.remainingLimit = limit;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getDiscount() {
        return discount;
    }

    public void setDiscount(int discount) {
        this.discount = discount;
    }

    public void setDiscount(String discount) {
        this.discount = Integer.parseInt(discount);
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
        this.remainingLimit = limit;
    }

    public BigDecimal getRemainingLimit() {
        return remainingLimit;
    }

    public void useAmount(BigDecimal amount) {
        this.remainingLimit = this.remainingLimit.subtract(amount);
    }

    public boolean isPoints() {
        return "PUNKTY".equals(id);
    }
}