package com.monogatari.app.dto.transaction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TransactionResponse {
    private Long id;
    private BigDecimal amount;
    private String username;
    private LocalDateTime createdAt;
    private String stripeInvoiceId;
    private String status;
    private String currency;
}