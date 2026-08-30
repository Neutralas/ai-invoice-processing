package com.simonas.invoiceprocessing.invoice.ai;

import com.simonas.invoiceprocessing.invoice.domain.Currency;
import com.simonas.invoiceprocessing.invoice.dto.ExtractedInvoice;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Temporary invoice extractor used for local development.
 *
 * <p>Returns a fixed invoice so that the processing workflow
 * can be developed before the LLM integration is implemented.</p>
 */
@Component
public class StubInvoiceExtractor implements InvoiceExtractor{

    @Override
    public ExtractedInvoice extract(String documentText) {
        return new ExtractedInvoice(
                "Example GmbH",
                "INV-123",
                LocalDate.of(2026,8,20),
                LocalDate.of(2026,9,15),
                BigDecimal.valueOf(750),
                Currency.EUR
        );
    }
}
