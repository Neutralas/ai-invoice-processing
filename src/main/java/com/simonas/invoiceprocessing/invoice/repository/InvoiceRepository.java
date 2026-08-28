package com.simonas.invoiceprocessing.invoice.repository;

import com.simonas.invoiceprocessing.invoice.domain.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
}
