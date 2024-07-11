package org.acme.billing;

import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.reactive.messaging.Message;
import org.eclipse.microprofile.reactive.messaging.Outgoing;

@ApplicationScoped
public class InvoiceProcessor {

    @Incoming("invoices")
    @Outgoing("invoices-requests")
    public Message<Invoice> processInvoice(Message<JsonObject> message) {
        final ReservationInvoice reservationInvoice = message.getPayload().mapTo(ReservationInvoice.class);
        final Reservation reservation  = reservationInvoice.reservation;

        final Invoice invoice = new Invoice(reservationInvoice.price, false, reservation);

        invoice.persist();

        message.ack();
        return Message.of(invoice);
    }
}
