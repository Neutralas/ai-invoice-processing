package com.simonas.invoiceprocessing.invoice.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "invoice")
@Getter
@NoArgsConstructor
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String supplier;

    @Column(name = "invoice_number")
    private String invoiceNumber;

    @Column(name = "invoice_date")
    private LocalDate invoiceDate;

    @Column(name = "due_date")
    private LocalDate dueDate;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision_reason")
    private DecisionReason decisionReason;

    @Column(name = "raw_ai_response")
    private String rawAiResponse;

    @Column(name = "processed_at")
    private Instant processedAt;

    public Invoice(String supplier, String invoiceNumber, LocalDate invoiceDate, LocalDate dueDate, BigDecimal amount, Currency currency, String rawAiResponse) {
        this.supplier = supplier;
        this.invoiceNumber = invoiceNumber;
        this.invoiceDate = invoiceDate;
        this.dueDate = dueDate;
        this.amount = amount;
        this.currency = currency;
        this.rawAiResponse = rawAiResponse;
    }

    public void approve(Instant processedAt) {
        this.status = InvoiceStatus.APPROVED;
        this.decisionReason = DecisionReason.AUTO_APPROVED;
        this.processedAt = processedAt;
    }

    public void markForManualReview(DecisionReason reason, Instant processedAt) {
        this.status = InvoiceStatus.MANUAL_REVIEW;
        this.decisionReason = reason;
        this.processedAt = processedAt;
    }
}
