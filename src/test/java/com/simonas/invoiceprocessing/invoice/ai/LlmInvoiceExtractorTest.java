package com.simonas.invoiceprocessing.invoice.ai;

import com.simonas.invoiceprocessing.invoice.domain.Currency;
import com.simonas.invoiceprocessing.invoice.dto.ExtractedInvoice;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest()
class LlmInvoiceExtractorTest {

    @Autowired
    private LlmInvoiceExtractor invoiceExtractor;

    @Test
    void shouldExtractInvoiceInformationFromDocument() {
        String invoiceText = """
                INVOICE

                Supplier: Example GmbH
                Invoice Number: INV-2026-001
                Invoice Date: 2026-08-20
                Due Date: 2026-09-15

                Total Amount: EUR 750.00
                """;

        ExtractedInvoice extractedInvoice =
                invoiceExtractor.extract(invoiceText);

        assertNotNull(extractedInvoice);
        assertEquals("Example GmbH", extractedInvoice.supplier());
        assertEquals("INV-2026-001", extractedInvoice.invoiceNumber());
        assertEquals(LocalDate.of(2026, 8, 20), extractedInvoice.invoiceDate());
        assertEquals(LocalDate.of(2026, 9, 15), extractedInvoice.dueDate());
        assertEquals(0, new BigDecimal("750.00").compareTo(extractedInvoice.amount()));
        assertEquals(Currency.EUR, extractedInvoice.currency());
    }
}
