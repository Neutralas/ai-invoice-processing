package com.simonas.invoiceprocessing.invoice;

import com.simonas.invoiceprocessing.invoice.domain.Currency;
import com.simonas.invoiceprocessing.invoice.domain.DecisionReason;
import com.simonas.invoiceprocessing.invoice.domain.Invoice;
import com.simonas.invoiceprocessing.invoice.domain.InvoiceStatus;
import com.simonas.invoiceprocessing.invoice.dto.InvoiceResponse;
import com.simonas.invoiceprocessing.invoice.dto.ProcessInvoiceRequest;
import com.simonas.invoiceprocessing.invoice.repository.InvoiceRepository;
import com.simonas.invoiceprocessing.invoice.service.InvoiceProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class InvoiceProcessingIntegrationTest {

    @Autowired
    private InvoiceProcessingService invoiceProcessingService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Test
    void shouldProcessValidInvoiceUsingLlm() {
        ProcessInvoiceRequest request = new ProcessInvoiceRequest("""
                INVOICE
                
                Supplier: Example GmbH
                Invoice Number: INV-2026-001
                Invoice Date: 2026-08-20
                Due Date: 2026-09-15
                
                Total Amount: EUR 750.00
                """);

        InvoiceResponse response = invoiceProcessingService.process(request);

        assertNotNull(response.invoiceId());
        assertEquals("Example GmbH", response.supplier());
        assertEquals("INV-2026-001", response.invoiceNumber());
        assertEquals(LocalDate.of(2026, 8, 20), response.invoiceDate());
        assertEquals(LocalDate.of(2026, 9, 15), response.dueDate());
        assertEquals(0, new BigDecimal("750.00").compareTo(response.amount()));
        assertEquals(Currency.EUR, response.currency());
        assertEquals(InvoiceStatus.APPROVED, response.status());
        assertEquals(DecisionReason.AUTO_APPROVED, response.decisionReason());
        assertNotNull(response.processedAt());

        Invoice savedInvoice = invoiceRepository.findById(response.invoiceId())
                .orElseThrow();

        assertEquals(response.invoiceId(), savedInvoice.getId());
        assertEquals(InvoiceStatus.APPROVED, savedInvoice.getStatus());
    }

    @Test
    void shouldSendHighValueInvoiceToManualReview() {
        ProcessInvoiceRequest request = new ProcessInvoiceRequest("""
                INVOICE
                
                Supplier: Example GmbH
                Invoice Number: INV-2026-002
                Invoice Date: 2026-08-20
                Due Date: 2026-09-15
                
                Total Amount: EUR 1500.00
                """);

        InvoiceResponse response = invoiceProcessingService.process(request);

        assertNotNull(response.invoiceId());
        assertEquals("Example GmbH", response.supplier());
        assertEquals("INV-2026-002", response.invoiceNumber());
        assertEquals(0, new BigDecimal("1500.00").compareTo(response.amount()));
        assertEquals(Currency.EUR, response.currency());
        assertEquals(InvoiceStatus.MANUAL_REVIEW, response.status());
        assertEquals(DecisionReason.AMOUNT_EXCEEDS_THRESHOLD, response.decisionReason());
        assertNotNull(response.processedAt());

        Invoice savedInvoice = invoiceRepository.findById(response.invoiceId())
                .orElseThrow();

        assertEquals(InvoiceStatus.MANUAL_REVIEW, savedInvoice.getStatus());
        assertEquals(DecisionReason.AMOUNT_EXCEEDS_THRESHOLD, savedInvoice.getDecisionReason());
    }

    @Test
    void shouldSendInvoiceToManualReviewWhenExtractedDataIsInvalid() {
        ProcessInvoiceRequest request = new ProcessInvoiceRequest("""
                INVOICE
                
                Supplier: Example GmbH
                Invoice Number: INV-2026-003
                
                Invoice Date: 2026-09-15
                Due Date: 2026-08-20
                
                Total Amount: EUR 750.00
                """);

        InvoiceResponse response = invoiceProcessingService.process(request);

        assertNotNull(response.invoiceId());
        assertEquals(InvoiceStatus.MANUAL_REVIEW, response.status());
        assertEquals(DecisionReason.INVALID_EXTRACTED_DATA, response.decisionReason());
        assertNotNull(response.processedAt());

        Invoice savedInvoice = invoiceRepository.findById(response.invoiceId())
                .orElseThrow();

        assertEquals(InvoiceStatus.MANUAL_REVIEW, savedInvoice.getStatus());
        assertEquals(DecisionReason.INVALID_EXTRACTED_DATA, savedInvoice.getDecisionReason());
    }
}
