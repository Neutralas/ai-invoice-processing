package com.simonas.invoiceprocessing.invoice.validation;

import com.simonas.invoiceprocessing.invoice.dto.ExtractedInvoice;

/**
 * Validates domain-level rules for extracted invoice data.
 *
 * <p>Field-level validation is handled by Jakarta Bean Validation.
 * This validator handles rules that depend on multiple invoice fields.</p>
 */
public class InvoiceValidator {

    /**
     * Checks that an invoice's due date is not before its invoice date.
     *
     * @param invoice the AI extracted invoice to validate
     * @return {@code true} when the dates are valid; otherwise {@code false}
     */
    public boolean isValid(ExtractedInvoice invoice) {
        return !invoice.dueDate().isBefore(invoice.invoiceDate());
    }
}
