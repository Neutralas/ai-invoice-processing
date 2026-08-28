package com.simonas.invoiceprocessing.invoice.dto;

import jakarta.validation.constraints.NotBlank;

public record ProcessInvoiceRequest(

        @NotBlank
        String documentText
) {}
