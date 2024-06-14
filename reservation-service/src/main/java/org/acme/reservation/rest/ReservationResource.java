package org.acme.reservation.rest;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import io.smallrye.graphql.client.GraphQLClient;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.inventory.InventoryClient;
import org.acme.reservation.rental.RentalClient;
import org.acme.reservation.reservation.Reservation;
import org.acme.reservation.reservation.ReservationsRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private final ReservationsRepository reservationsRepository;
    private final InventoryClient inventoryClient;
    private final RentalClient rentalClient;
    private final SecurityContext context;

    public ReservationResource(ReservationsRepository reservations,
                               @GraphQLClient("inventory") GraphQLInventoryClient inventoryClient,
                               @RestClient RentalClient rentalClient,
                               SecurityContext context) {
        this.reservationsRepository = reservations;
        this.inventoryClient = inventoryClient;
        this.rentalClient = rentalClient;
        this.context = context;
    }

    @GET
    @Path("/all")
    public Collection<Reservation> allReservations() {
        final String userId = context.getUserPrincipal() != null ? context.getUserPrincipal().getName() : "anonimo";

        return reservationsRepository.findAll()
                .stream()
                .filter(r -> userId == null || userId.equals(r.userId))
                .collect(Collectors.toList());
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    public Reservation make(Reservation reservation) {
        reservation.userId = context.getUserPrincipal() != null ? context.getUserPrincipal().getName() : "anonimo";
        final Reservation result = reservationsRepository.save(reservation);

        if (reservation.startDay.equals(LocalDate.now())) {
            rentalClient.start(reservation.userId, result.id);
        }

        return result;
    }

    @GET
    @Path("availability")
    public Collection<Car> availability(@RestQuery LocalDate startDate,
                                        @RestQuery LocalDate endDate) {
        final List<Car> availableCars = inventoryClient.allCars();
        final Map<Long, Car> carsById = new HashMap<>();
        for (Car car : availableCars) {
            carsById.put(car.id, car);
        }

        final List<Reservation> reservations = reservationsRepository.findAll();
        for (Reservation reservation : reservations) {
            if (reservation.isReserved(startDate, endDate)) {
                carsById.remove(reservation.carId);
            }
        }

        return carsById.values();
    }

    @DELETE
    public void removeReserve() {
        final String userId = context.getUserPrincipal() != null ? context.getUserPrincipal().getName() : "anonimo";
        var reservations = reservationsRepository.findAll();
        reservations.removeIf(f -> f.userId.equals(userId));
    }
}
