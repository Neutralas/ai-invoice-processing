package com.simonas.invoiceprocessing.invoice.service;

import com.simonas.invoiceprocessing.invoice.domain.Currency;
import com.simonas.invoiceprocessing.invoice.domain.DecisionReason;
import com.simonas.invoiceprocessing.invoice.domain.InvoiceStatus;
import com.simonas.invoiceprocessing.invoice.domain.ProcessingDecision;
import com.simonas.invoiceprocessing.invoice.dto.ExtractedInvoice;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceApprovalServiceTest {

    private final InvoiceApprovalService invoiceApprovalService = new InvoiceApprovalService();

    @Test
    void shouldApproveInvoiceBelowAutomaticApprovalThreshold() {
        ExtractedInvoice invoice = createInvoice(new BigDecimal("999.99"));

        ProcessingDecision decision = invoiceApprovalService.decide(invoice);

        assertEquals(InvoiceStatus.APPROVED, decision.status());
        assertEquals(DecisionReason.AUTO_APPROVED, decision.reason());
    }

    @Test
    void shouldApproveInvoiceAtAutomaticApprovalThreshold() {
        ExtractedInvoice invoice = createInvoice(new BigDecimal("1000.00"));

        ProcessingDecision decision = invoiceApprovalService.decide(invoice);

        assertEquals(InvoiceStatus.APPROVED, decision.status());
        assertEquals(DecisionReason.AUTO_APPROVED, decision.reason());
    }

    @Test
    void shouldRequireManualReviewAboveAutomaticApprovalThreshold() {
        ExtractedInvoice invoice = createInvoice(new BigDecimal("1000.01"));

        ProcessingDecision decision = invoiceApprovalService.decide(invoice);

        assertEquals(InvoiceStatus.MANUAL_REVIEW, decision.status());
        assertEquals(DecisionReason.AMOUNT_EXCEEDS_THRESHOLD, decision.reason());
    }

    private ExtractedInvoice createInvoice(BigDecimal amount) {
        return new ExtractedInvoice(
                "Example GmbH",
                "INV-123",
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 9, 15),
                amount,
                Currency.EUR
        );
    }
}