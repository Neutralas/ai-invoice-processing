package com.simonas.invoiceprocessing.invoice.ai;

import com.simonas.invoiceprocessing.invoice.dto.ExtractedInvoice;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class LlmInvoiceExtractor implements InvoiceExtractor {

    private final ChatClient chatClient;

    public LlmInvoiceExtractor(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public ExtractedInvoice extract(String documentText) {

        String system = """
                You are an invoice information extraction assistant.
                
                Extract the following information from the invoice provided by the user:
                - supplier: the company or organisation that issued the invoice
                - invoiceNumber: the invoice's unique reference number
                - invoiceDate: the date the invoice was issued
                - dueDate: the payment due date
                - amount: the total invoice amount
                - currency: the invoice currency

                For dates, extract the exact date explicitly stated in the invoice.
                Do not infer, estimate, or transform the date based on other information.
                Do not infer or invent information.
                If a value is not explicitly present in the document, return null.
                Only use EUR, GBP, or USD for currency.
                Extract the invoice total, not individual line-item amounts.
                """;

        return chatClient.prompt()
                .system(system)
                .user(documentText)
                .call()
                .entity(ExtractedInvoice.class,
                        spec -> spec.useProviderStructuredOutput());
    }
}
