package org.acme.rental.reservation;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import org.acme.rental.entity.Reservation;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "reservation-api")
@Path("/admin/reservation")
public interface ReservationClient {

    @GET
    @Path("/{id}")
    Reservation getReservation(@PathParam("id") Long id);
}
