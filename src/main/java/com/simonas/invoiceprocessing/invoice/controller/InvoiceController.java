package com.simonas.invoiceprocessing.invoice.controller;

import com.simonas.invoiceprocessing.invoice.dto.InvoiceResponse;
import com.simonas.invoiceprocessing.invoice.dto.ProcessInvoiceRequest;
import com.simonas.invoiceprocessing.invoice.service.InvoiceProcessingService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/invoices")
public class InvoiceController {

    private final InvoiceProcessingService invoiceProcessingService;

    public InvoiceController(InvoiceProcessingService invoiceProcessingService) {
        this.invoiceProcessingService = invoiceProcessingService;
    }

    @PostMapping("/process")
    public InvoiceResponse process(
            @Valid @RequestBody ProcessInvoiceRequest processInvoiceRequest) {
        return invoiceProcessingService.process(processInvoiceRequest);
    }
}
