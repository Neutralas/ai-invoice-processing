package com.simonas.invoiceprocessing.invoice.service;

import com.simonas.invoiceprocessing.invoice.dto.InvoiceResponse;
import com.simonas.invoiceprocessing.invoice.dto.ProcessInvoiceRequest;
import org.springframework.stereotype.Service;

/**
 * Coordinates the invoice processing workflow.
 *
 * <p>The processing workflow will extract invoice data, validate it,
 * apply business rules, and persist the resulting invoice.</p>
 */
@Service
public class InvoiceProcessingService {

    /**
     * Processes an invoice document.
     *
     * @param processInvoiceRequest the invoice document to process
     * @return the processing result
     */
    public InvoiceResponse process(ProcessInvoiceRequest processInvoiceRequest) {
        return null;
    }
}
