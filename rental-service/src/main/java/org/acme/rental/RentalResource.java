package org.acme.rental;

import io.quarkus.logging.Log;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.acme.rental.billing.InvoiceAdjust;
import org.acme.rental.entity.Rental;
import org.acme.rental.entity.Reservation;
import org.acme.rental.reservation.ReservationClient;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/rental")
public class RentalResource {

    private static final double STANDARD_REFUND_RATE_PER_DAY = -10.99;
    private static final double STANDARD_PRICE_FOR_PROLONGED_DAY = 25.99;
    private static final Logger log = LoggerFactory.getLogger(RentalResource.class);

    @Inject
    @Channel("invoices-adjust")
    Emitter<InvoiceAdjust> invoiceAdjustEmitter;

    @Inject
    @RestClient
    ReservationClient client;

    @Path("/start/{userId}/{reservationId}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Rental start(String userId,
                        Long reservationId) {
        Log.infof("Starting rental for %s with reservation %s",
                userId, reservationId);

        Rental rental = new Rental();
        rental.userId = userId;
        rental.reservationId = reservationId;
        rental.startDate = LocalDate.now();
        rental.active = true;

        rental.persist();

        return rental;
    }

    @PUT
    @Path("/end/{userId}/{reservationId}")
    public Rental end(String userId, Long reservationId) {
        Log.infof("Ending rental for %s with reservation %s",
                userId, reservationId);
        Rental rental = Rental
                .findByUserAndReservationIdsOptional(userId, reservationId)
                .orElseThrow(() -> new NotFoundException("Rental not found"));

        Reservation reservation = client.getReservation(reservationId);
        log.info("reservattion {}", reservation);

        LocalDate today = LocalDate.now();

        if (!reservation.endDay.isEqual(today)) {
            invoiceAdjustEmitter.send(new InvoiceAdjust(rental.id.toString(), userId, today, computePrice(reservation.endDay, today)));
            log.info("send message");
        }

        rental.endDate = LocalDate.now();
        rental.active = false;
        rental.update();
        return rental;

    }

    @GET
    public List<Rental> list() {
        return Rental.listAll();
    }

    @GET
    @Path("/active")
    public List<Rental> listActive() {
        return Rental.listActive();
    }

    //@Incoming("invoices-adjust")
    public void test(InvoiceAdjust invoiceAdjust) {
        log.info("receive message {}", invoiceAdjust);
    }

    private double computePrice(LocalDate endDate, LocalDate today) {
        return endDate.isBefore(today) ?
                ChronoUnit.DAYS.between(endDate, today)
                        * STANDARD_PRICE_FOR_PROLONGED_DAY :
                ChronoUnit.DAYS.between(today, endDate)
                        * STANDARD_REFUND_RATE_PER_DAY;
    }
}