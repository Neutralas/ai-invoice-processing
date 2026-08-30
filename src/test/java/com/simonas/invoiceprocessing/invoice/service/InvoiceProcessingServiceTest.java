package com.simonas.invoiceprocessing.invoice.service;

import com.simonas.invoiceprocessing.invoice.ai.InvoiceExtractor;
import com.simonas.invoiceprocessing.invoice.domain.*;
import com.simonas.invoiceprocessing.invoice.dto.ExtractedInvoice;
import com.simonas.invoiceprocessing.invoice.dto.InvoiceResponse;
import com.simonas.invoiceprocessing.invoice.dto.ProcessInvoiceRequest;
import com.simonas.invoiceprocessing.invoice.repository.InvoiceRepository;
import com.simonas.invoiceprocessing.invoice.validation.InvoiceValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvoiceProcessingServiceTest {

    @Mock
    private Validator validator;
    @Mock
    private InvoiceExtractor invoiceExtractor;
    @Mock
    private InvoiceValidator invoiceValidator;
    @Mock
    private InvoiceApprovalService invoiceApprovalService;
    @Mock
    private InvoiceRepository invoiceRepository;

    private InvoiceProcessingService invoiceProcessingService;

    @BeforeEach
    void setUp() {
        invoiceProcessingService = new InvoiceProcessingService(
                validator,
                invoiceExtractor,
                invoiceValidator,
                invoiceApprovalService,
                invoiceRepository
        );
    }

    @Test
    void shouldApproveValidInvoice() {
        ProcessInvoiceRequest request =
                new ProcessInvoiceRequest("Invoice from Example GmbH");

        ExtractedInvoice extractedInvoice =
                createInvoice(new BigDecimal("750.00"));

        when(invoiceExtractor.extract(request.documentText()))
                .thenReturn(extractedInvoice);
        when(validator.validate(extractedInvoice))
                .thenReturn(Set.of());
        when(invoiceValidator.isValid(extractedInvoice))
                .thenReturn(true);
        when(invoiceApprovalService.decide(extractedInvoice))
                .thenReturn(new ProcessingDecision(
                        InvoiceStatus.APPROVED,
                        DecisionReason.AUTO_APPROVED
                ));
        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InvoiceResponse response =
                invoiceProcessingService.process(request);

        assertEquals(InvoiceStatus.APPROVED, response.status());
        assertEquals(DecisionReason.AUTO_APPROVED, response.decisionReason());

        verify(invoiceApprovalService).decide(extractedInvoice);
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void shouldRequireManualReviewWhenApprovalDecisionRequiresIt() {
        ProcessInvoiceRequest request =
                new ProcessInvoiceRequest("Invoice from Example GmbH");

        ExtractedInvoice extractedInvoice =
                createInvoice(new BigDecimal("1500.00"));

        when(invoiceExtractor.extract(request.documentText()))
                .thenReturn(extractedInvoice);
        when(validator.validate(extractedInvoice))
                .thenReturn(Set.of());
        when(invoiceValidator.isValid(extractedInvoice))
                .thenReturn(true);
        when(invoiceApprovalService.decide(extractedInvoice))
                .thenReturn(new ProcessingDecision(
                        InvoiceStatus.MANUAL_REVIEW,
                        DecisionReason.AMOUNT_EXCEEDS_THRESHOLD
                ));
        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InvoiceResponse response =
                invoiceProcessingService.process(request);

        assertEquals(InvoiceStatus.MANUAL_REVIEW, response.status());
        assertEquals(DecisionReason.AMOUNT_EXCEEDS_THRESHOLD, response.decisionReason());

        verify(invoiceApprovalService).decide(extractedInvoice);
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void shouldRequireManualReviewWhenBeanValidationFails() {
        ProcessInvoiceRequest request =
                new ProcessInvoiceRequest("Incomplete invoice");

        ExtractedInvoice extractedInvoice =
                createInvoice(new BigDecimal("750.00"));

        Set<ConstraintViolation<ExtractedInvoice>> violations =
                Set.of(anyConstraintViolation());

        when(invoiceExtractor.extract(request.documentText()))
                .thenReturn(extractedInvoice);
        when(validator.validate(extractedInvoice))
                .thenReturn(violations);
        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InvoiceResponse response =
                invoiceProcessingService.process(request);

        assertEquals(InvoiceStatus.MANUAL_REVIEW, response.status());
        assertEquals(DecisionReason.INVALID_EXTRACTED_DATA, response.decisionReason());

        verify(invoiceApprovalService, never()).decide(any());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void shouldRequireManualReviewWhenDomainValidationFails() {
        ProcessInvoiceRequest request =
                new ProcessInvoiceRequest("Invoice with invalid dates");

        ExtractedInvoice extractedInvoice =
                createInvoice(new BigDecimal("750.00"));

        when(invoiceExtractor.extract(request.documentText()))
                .thenReturn(extractedInvoice);
        when(validator.validate(extractedInvoice))
                .thenReturn(Set.of());
        when(invoiceValidator.isValid(extractedInvoice))
                .thenReturn(false);
        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InvoiceResponse response =
                invoiceProcessingService.process(request);

        assertEquals(InvoiceStatus.MANUAL_REVIEW, response.status());
        assertEquals(DecisionReason.INVALID_EXTRACTED_DATA, response.decisionReason());

        verify(invoiceApprovalService, never()).decide(any());
        verify(invoiceRepository).save(any(Invoice.class));
    }

    @Test
    void shouldSaveExtractedValuesWhenValidationFails() {
        ProcessInvoiceRequest request =
                new ProcessInvoiceRequest("Partially extracted invoice");

        ExtractedInvoice extractedInvoice =
                new ExtractedInvoice(
                        null,
                        "INV-123",
                        LocalDate.of(2026, 8, 20),
                        LocalDate.of(2026, 9, 15),
                        new BigDecimal("750.00"),
                        Currency.EUR
                );

        when(invoiceExtractor.extract(request.documentText()))
                .thenReturn(extractedInvoice);
        when(validator.validate(extractedInvoice))
                .thenReturn(Set.of(anyConstraintViolation()));
        when(invoiceRepository.save(any(Invoice.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        InvoiceResponse response =
                invoiceProcessingService.process(request);

        assertNull(response.supplier());
        assertEquals("INV-123", response.invoiceNumber());
        assertEquals(new BigDecimal("750.00"), response.amount());
        assertEquals(InvoiceStatus.MANUAL_REVIEW, response.status());
        assertEquals(DecisionReason.INVALID_EXTRACTED_DATA, response.decisionReason());
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

    @SuppressWarnings("unchecked")
    private ConstraintViolation<ExtractedInvoice> anyConstraintViolation() {
        return (ConstraintViolation<ExtractedInvoice>) mock(ConstraintViolation.class);
    }
}