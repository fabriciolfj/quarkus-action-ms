package org.acme.reservation.billing;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationScoped
public class BillingService {

    private final Logger log = LoggerFactory.getLogger(BillingService.class);

    //@Incoming("invoices-rabbitmq")
    public void processInvoice(JsonObject json) {
        Invoice invoice = json.mapTo(Invoice.class);
        log.info("Invoice received: {}", invoice);
    }
}
