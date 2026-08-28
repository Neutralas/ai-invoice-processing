package com.simonas.invoiceprocessing.invoice.controller;

import com.simonas.invoiceprocessing.invoice.domain.Currency;
import com.simonas.invoiceprocessing.invoice.domain.DecisionReason;
import com.simonas.invoiceprocessing.invoice.domain.InvoiceStatus;
import com.simonas.invoiceprocessing.invoice.dto.InvoiceResponse;
import com.simonas.invoiceprocessing.invoice.dto.ProcessInvoiceRequest;
import com.simonas.invoiceprocessing.invoice.service.InvoiceProcessingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InvoiceController.class)
class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InvoiceProcessingService invoiceProcessingService;

    @Test
    void shouldProcessValidInvoiceRequest() throws Exception {
        InvoiceResponse response = new InvoiceResponse(
                1L,
                "Example GmbH",
                "INV-123",
                LocalDate.of(2026, 8, 20),
                LocalDate.of(2026, 9, 15),
                new BigDecimal("750.00"),
                Currency.EUR,
                InvoiceStatus.APPROVED,
                DecisionReason.AUTO_APPROVED,
                Instant.parse("2026-08-28T10:00:00Z")
        );

        when(invoiceProcessingService.process(any(ProcessInvoiceRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/api/v1/invoices/process")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "documentText": "Invoice from Example GmbH"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "invoiceId": 1,
                            "supplier": "Example GmbH",
                            "invoiceNumber": "INV-123",
                            "invoiceDate": "2026-08-20",
                            "dueDate": "2026-09-15",
                            "amount": 750.00,
                            "currency": "EUR",
                            "status": "APPROVED",
                            "decisionReason": "AUTO_APPROVED",
                            "processedAt": "2026-08-28T10:00:00Z"
                        }
                        """));

        verify(invoiceProcessingService).process(any(ProcessInvoiceRequest.class));
    }

    @Test
    void shouldReturnBadRequestWhenDocumentTextIsBlank() throws Exception {
        mockMvc.perform(post("/api/v1/invoices/process")
                        .contentType(APPLICATION_JSON)
                        .content("""
                                {
                                    "documentText": "   "
                                }
                                """))
                .andExpect(status().isBadRequest());

        verify(invoiceProcessingService, never())
                .process(any(ProcessInvoiceRequest.class));
    }
}