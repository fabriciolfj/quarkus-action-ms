package org.acme.rental.service;

import io.quarkus.logging.Log;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.NotFoundException;
import org.acme.rental.data.InvoiceConfirmation;
import org.acme.rental.data.Reservation;
import org.acme.rental.entity.Rental;
import org.eclipse.microprofile.reactive.messaging.Incoming;

@ApplicationScoped
public class InvoiceConfirmationService {

    @Incoming("invoices-confirmations")
    @Blocking
    public void invoicePaid(InvoiceConfirmation invoiceConfirmation) {
        Log.info("Received invoice confirmation " + invoiceConfirmation);

        if (!invoiceConfirmation.paid) {
            Log.warn("Received unpaid invoice confirmation - "
                    + invoiceConfirmation);
            // retry handling omitted
        }

        Reservation reservation =
                invoiceConfirmation.invoice.reservation;

        Rental.findByUserAndReservationIdsOptional(
                        reservation.userId, reservation.id)
                .ifPresentOrElse(rental -> {
                    // mark the already started rental as paid
                    rental.paid = true;
                    rental.update();
                }, () -> {
                    // create new rental starting in the future
                    Rental rental = new Rental();
                    rental.userId = reservation.userId;
                    rental.reservationId = reservation.id;
                    rental.startDate = reservation.startDay;
                    rental.active = false;
                    rental.paid = true;
                    rental.persist();
                });
    }

    public Rental end(String userId, Long reservationId) {
        Rental rental = Rental
                .findByUserAndReservationIdsOptional(userId, reservationId)
                .orElseThrow(() -> new NotFoundException("Rental not found"));

        if (!rental.paid) {
            Log.warn("Rental is not paid: " + rental);
            // trigger error processing
        }

        return rental;
    }
}