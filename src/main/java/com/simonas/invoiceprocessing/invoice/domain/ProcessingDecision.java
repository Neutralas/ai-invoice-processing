package com.simonas.invoiceprocessing.invoice.domain;

public record ProcessingDecision(
        InvoiceStatus status,
        DecisionReason reason
) {}
