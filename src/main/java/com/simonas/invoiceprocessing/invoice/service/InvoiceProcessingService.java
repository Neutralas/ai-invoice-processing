package com.simonas.invoiceprocessing.invoice.service;

import com.simonas.invoiceprocessing.invoice.ai.InvoiceExtractor;
import com.simonas.invoiceprocessing.invoice.domain.Invoice;
import com.simonas.invoiceprocessing.invoice.domain.ProcessingDecision;
import com.simonas.invoiceprocessing.invoice.dto.ExtractedInvoice;
import com.simonas.invoiceprocessing.invoice.dto.InvoiceResponse;
import com.simonas.invoiceprocessing.invoice.dto.ProcessInvoiceRequest;
import com.simonas.invoiceprocessing.invoice.repository.InvoiceRepository;
import com.simonas.invoiceprocessing.invoice.validation.InvoiceValidator;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;

import static com.simonas.invoiceprocessing.invoice.domain.DecisionReason.INVALID_EXTRACTED_DATA;
import static com.simonas.invoiceprocessing.invoice.domain.InvoiceStatus.APPROVED;

/**
 * Coordinates the invoice processing workflow.
 *
 * <p>The processing workflow will extract invoice data, validate it,
 * apply business rules, and persist the resulting invoice.</p>
 */
@Service
public class InvoiceProcessingService {

    private final Validator validator;
    private final InvoiceExtractor invoiceExtractor;
    private final InvoiceValidator invoiceValidator;
    private final InvoiceApprovalService invoiceApprovalService;
    private final InvoiceRepository invoiceRepository;

    public InvoiceProcessingService(Validator validator, InvoiceExtractor invoiceExtractor, InvoiceValidator invoiceValidator, InvoiceApprovalService invoiceApprovalService, InvoiceRepository invoiceRepository) {
        this.validator = validator;
        this.invoiceExtractor = invoiceExtractor;
        this.invoiceValidator = invoiceValidator;
        this.invoiceApprovalService = invoiceApprovalService;
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Processes an invoice document and determines its workflow outcome.
     *
     * <p>Invalid AI-extracted data is persisted for manual review.</p>
     *
     * @param processInvoiceRequest the invoice document to process
     * @return the processed invoice result
     */
    public InvoiceResponse process(ProcessInvoiceRequest processInvoiceRequest) {

        ExtractedInvoice extractedInvoice = invoiceExtractor.extract(processInvoiceRequest.documentText());
        Invoice invoice = toInvoice(extractedInvoice);
        Instant processedAt = Instant.now();

        Set<ConstraintViolation<ExtractedInvoice>> violations = validator.validate(extractedInvoice);
        boolean valid = violations.isEmpty() && invoiceValidator.isValid(extractedInvoice);

        if (!valid) {
            invoice.markForManualReview(INVALID_EXTRACTED_DATA, processedAt);
        } else {
            ProcessingDecision decision = invoiceApprovalService.decide(extractedInvoice);
            if (APPROVED.equals(decision.status())) {
                invoice.approve(processedAt);
            } else {
                invoice.markForManualReview(decision.reason(), processedAt);
            }
        }

        Invoice savedInvoice = invoiceRepository.save(invoice);
        return toInvoiceResponse(savedInvoice);
    }

    private Invoice toInvoice(ExtractedInvoice extractedInvoice) {
        return new Invoice(
                extractedInvoice.supplier(),
                extractedInvoice.invoiceNumber(),
                extractedInvoice.invoiceDate(),
                extractedInvoice.dueDate(),
                extractedInvoice.amount(),
                extractedInvoice.currency(),
                null
        );
    }

    private InvoiceResponse toInvoiceResponse(Invoice invoice) {
        return new InvoiceResponse(
                invoice.getId(),
                invoice.getSupplier(),
                invoice.getInvoiceNumber(),
                invoice.getInvoiceDate(),
                invoice.getDueDate(),
                invoice.getAmount(),
                invoice.getCurrency(),
                invoice.getStatus(),
                invoice.getDecisionReason(),
                invoice.getProcessedAt()
        );
    }
}
