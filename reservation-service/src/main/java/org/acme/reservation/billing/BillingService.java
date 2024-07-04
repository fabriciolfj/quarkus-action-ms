package org.acme.reservation.billing;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BillingService {

    private final Logger log = LoggerFactory.getLogger(BillingService.class);

    @Incoming("invoices")
    public void processInvoice(Invoice invoice) {
        log.info("Invoice received: {}", invoice);
    }
}
