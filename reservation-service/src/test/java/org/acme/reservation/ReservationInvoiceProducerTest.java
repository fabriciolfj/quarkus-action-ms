package org.acme.reservation;

import io.quarkus.test.junit.QuarkusTest;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.MediaType;
import org.acme.reservation.billing.Invoice;
import org.acme.reservation.entity.Reservation;
import org.awaitility.Awaitility;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static io.restassured.RestAssured.given;
import static org.acme.reservation.rest.ReservationResource.STANDARD_RATE_PER_DAY;

@QuarkusTest
@ApplicationScoped
public class ReservationInvoiceProducerTest {

    private final Map<Integer, Invoice> receivedInvoices = new HashMap<>();
    private final AtomicInteger ids = new AtomicInteger(0);

    @Incoming("invoices-rabbitmq")
    public void processInvoice(JsonObject json) {
        Invoice invoice = json.mapTo(Invoice.class);
        System.out.println("Received invoice " + invoice);

        receivedInvoices.put(ids.incrementAndGet(), invoice);
    }

    @Test
    public void testInvoiceProduced() throws Throwable {
        // Make a reservation request that sends the invoice to RabbitMQ
        Reservation reservation = new Reservation();
        reservation.carId = 1L;
        reservation.startDay = LocalDate.now().plusDays(1);
        reservation.endDay = reservation.startDay;

        given().body(reservation).contentType(MediaType.APPLICATION_JSON)
                .when().post("/reservation")
                .then().statusCode(200);

        Awaitility.await().atMost(5, TimeUnit.SECONDS)
                .until(() -> receivedInvoices.size() == 1);

        // Assert that the invoice message was received in this consumer
        Assertions.assertEquals(1, receivedInvoices.size());
        Assertions.assertEquals(STANDARD_RATE_PER_DAY,
                receivedInvoices.get(1).price);
    }
}