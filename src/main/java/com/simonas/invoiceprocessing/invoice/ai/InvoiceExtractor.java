package com.simonas.invoiceprocessing.invoice.ai;

import com.simonas.invoiceprocessing.invoice.dto.ExtractedInvoice;

public interface InvoiceExtractor {

    ExtractedInvoice extract(String documentText);
}
