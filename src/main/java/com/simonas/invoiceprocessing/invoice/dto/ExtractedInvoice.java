package com.simonas.invoiceprocessing.invoice.dto;

import com.simonas.invoiceprocessing.invoice.domain.Currency;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExtractedInvoice(

        @NotBlank
        String supplier,

        @NotBlank
        String invoiceNumber,

        @NotNull
        LocalDate invoiceDate,

        @NotNull
        LocalDate dueDate,

        @Positive
        BigDecimal amount,

        @NotNull
        Currency currency
) {}
