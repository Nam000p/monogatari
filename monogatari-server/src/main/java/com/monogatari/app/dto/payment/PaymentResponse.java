package com.monogatari.app.dto.payment;

import lombok.Data;

@Data
public class PaymentResponse {
    private boolean success;
    private String message;
    private Object data;

    public PaymentResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}