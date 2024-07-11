package org.ocme.rental;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusMock;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.kafka.InjectKafkaCompanion;
import io.quarkus.test.kafka.KafkaCompanionResource;
import io.restassured.http.ContentType;
import io.smallrye.reactive.messaging.kafka.companion.ConsumerTask;
import io.smallrye.reactive.messaging.kafka.companion.KafkaCompanion;
import org.acme.rental.entity.Reservation;
import org.acme.rental.reservation.ReservationClient;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@QuarkusTestResource(KafkaCompanionResource.class)
public class RentalResourceTest {

    @InjectKafkaCompanion
    KafkaCompanion kafkaCompanion;

    @Test
    public void testRentalProlongedInvoiceSend() {
        // stub the ReservationClient call
        Reservation reservation = new Reservation();
        reservation.endDay = LocalDate.now().minusDays(1);

        ReservationClient mock = Mockito.mock(ReservationClient.class);
        Mockito.when(mock.getReservation(1L)).thenReturn(reservation);
        QuarkusMock.installMockForType(mock, ReservationClient.class,
                RestClient.LITERAL);

        // start new Rental for reservation with id 1
        given()
                .when()
                .contentType(ContentType.JSON)
                .post("/rental/start/user123/1")
                .then().statusCode(200);

        // end the with one prolonged day
        given()
                .contentType(ContentType.JSON)
                .when().put("/rental/end/user123/1")
                .then().statusCode(200)
                .body("active", is(false),
                        "endDate", is(LocalDate.now().toString()));

        // verify that message is sent to the invoices-adjust Kafka topic
        ConsumerTask<String, String> invoiceAdjust = kafkaCompanion
                .consumeStrings().fromTopics("invoices-adjust", 1)
                .awaitNextRecord(Duration.ofSeconds(15));

        assertEquals(1, invoiceAdjust.count());
    }
}