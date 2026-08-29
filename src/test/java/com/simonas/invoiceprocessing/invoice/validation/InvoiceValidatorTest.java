package com.simonas.invoiceprocessing.invoice.validation;

import com.simonas.invoiceprocessing.invoice.domain.Currency;
import com.simonas.invoiceprocessing.invoice.dto.ExtractedInvoice;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class InvoiceValidatorTest {

    private final InvoiceValidator invoiceValidator = new InvoiceValidator();

    @Test
    void shouldBeValidWhenDueDateIsAfterInvoiceDate() {
        ExtractedInvoice invoice = createInvoice(
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 9, 15)
        );

        assertTrue(invoiceValidator.isValid(invoice));
    }

    @Test
    void shouldBeValidWhenDueDateEqualsInvoiceDate() {
        LocalDate date = LocalDate.of(2026, 8, 20);
        ExtractedInvoice invoice = createInvoice(date, date);

        assertTrue(invoiceValidator.isValid(invoice));
    }

    @Test
    void shouldBeInvalidWhenDueDateIsBeforeInvoiceDate() {
        ExtractedInvoice invoice = createInvoice(
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 8, 19)
        );

        assertFalse(invoiceValidator.isValid(invoice));
    }

    private ExtractedInvoice createInvoice(
            LocalDate invoiceDate,
            LocalDate dueDate
    ) {
        return new ExtractedInvoice(
                "Example GmbH",
                "INV-123",
                invoiceDate,
                dueDate,
                new BigDecimal("750.00"),
                Currency.EUR
        );
    }
}