package com.simonas.invoiceprocessing.invoice.service;

import com.simonas.invoiceprocessing.invoice.domain.DecisionReason;
import com.simonas.invoiceprocessing.invoice.domain.InvoiceStatus;
import com.simonas.invoiceprocessing.invoice.domain.ProcessingDecision;
import com.simonas.invoiceprocessing.invoice.dto.ExtractedInvoice;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Determines the automated approval outcome for a valid extracted invoice.
 *
 * <p>Invoices at or below the automatic approval threshold are approved.
 * Invoices above the threshold require manual review.</p>
 */
@Service
public class InvoiceApprovalService {

    private static final BigDecimal AUTO_APPROVAL_THRESHOLD = new BigDecimal("1000.00");

    /**
     * Determines the workflow decision for an extracted invoice.
     *
     * @param extractedInvoice validated invoice data
     * @return the status and reason resulting from the approval rules
     */
    public ProcessingDecision decide(ExtractedInvoice extractedInvoice) {

        int comparison = extractedInvoice.amount().compareTo(AUTO_APPROVAL_THRESHOLD);

        if (comparison <= 0) {
            return new ProcessingDecision(
                    InvoiceStatus.APPROVED,
                    DecisionReason.AUTO_APPROVED
            );
        }

        return new ProcessingDecision(
                InvoiceStatus.MANUAL_REVIEW,
                DecisionReason.AMOUNT_EXCEEDS_THRESHOLD
        );
    }
}
