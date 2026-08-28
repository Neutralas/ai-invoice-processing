package com.simonas.invoiceprocessing.invoice.dto;

import com.simonas.invoiceprocessing.invoice.domain.Currency;
import com.simonas.invoiceprocessing.invoice.domain.DecisionReason;
import com.simonas.invoiceprocessing.invoice.domain.InvoiceStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

public record InvoiceResponse(

        Long invoiceId,
        String supplier,
        String invoiceNumber,
        LocalDate invoiceDate,
        LocalDate dueDate,
        BigDecimal amount,
        Currency currency,
        InvoiceStatus status,
        DecisionReason decisionReason,
        Instant processedAt
) {}
