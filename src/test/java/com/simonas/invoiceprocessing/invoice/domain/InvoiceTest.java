package com.simonas.invoiceprocessing.invoice.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceTest {

    @Test
    void approve_shouldSetApprovedStatusAndReasonAndProcessedAt() {
        Invoice invoice = createInvoice();
        Instant processedAt = Instant.parse("2026-08-28T10:00:00Z");

        invoice.approve(processedAt);

        assertEquals(InvoiceStatus.APPROVED, invoice.getStatus());
        assertEquals(DecisionReason.AUTO_APPROVED, invoice.getDecisionReason());
        assertEquals(processedAt, invoice.getProcessedAt());
    }

    @Test
    void markForManualReview_shouldSetManualReviewStatusWithAmountThresholdReason() {
        Invoice invoice = createInvoice();
        Instant processedAt = Instant.parse("2026-08-28T10:00:00Z");

        invoice.markForManualReview(
                DecisionReason.AMOUNT_EXCEEDS_THRESHOLD,
                processedAt
        );

        assertEquals(InvoiceStatus.MANUAL_REVIEW, invoice.getStatus());
        assertEquals(DecisionReason.AMOUNT_EXCEEDS_THRESHOLD, invoice.getDecisionReason());
        assertEquals(processedAt, invoice.getProcessedAt());
    }

    @Test
    void markForManualReview_shouldSetManualReviewStatusWithInvalidExtractionReason() {
        Invoice invoice = createInvoice();
        Instant processedAt = Instant.parse("2026-08-28T10:00:00Z");

        invoice.markForManualReview(
                DecisionReason.INVALID_EXTRACTED_DATA,
                processedAt
        );

        assertEquals(InvoiceStatus.MANUAL_REVIEW, invoice.getStatus());
        assertEquals(DecisionReason.INVALID_EXTRACTED_DATA, invoice.getDecisionReason());
        assertEquals(processedAt, invoice.getProcessedAt());
    }

    private Invoice createInvoice() {
        return new Invoice(
                "Example GmbH",
                "INV-123",
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 9, 15),
                new BigDecimal("750.00"),
                Currency.EUR,
                null
        );
    }
}