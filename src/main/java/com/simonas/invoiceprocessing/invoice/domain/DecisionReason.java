package com.simonas.invoiceprocessing.invoice.domain;

public enum DecisionReason {
    AUTO_APPROVED,
    AMOUNT_EXCEEDS_THRESHOLD,
    INVALID_EXTRACTED_DATA
}
