package org.acme.reservation.rest;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

import io.smallrye.graphql.client.GraphQLClient;
import jakarta.ws.rs.core.SecurityContext;
import org.acme.reservation.inventory.Car;
import org.acme.reservation.inventory.GraphQLInventoryClient;
import org.acme.reservation.inventory.InventoryClient;
import org.acme.reservation.rental.Rental;
import org.acme.reservation.rental.RentalClient;
import org.acme.reservation.entity.Reservation;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestQuery;

@Path("reservation")
@Produces(MediaType.APPLICATION_JSON)
public class ReservationResource {

    private final InventoryClient inventoryClient;
    private final RentalClient rentalClient;
    private final SecurityContext context;

    public ReservationResource(@GraphQLClient("inventory") GraphQLInventoryClient inventoryClient,
                               @RestClient RentalClient rentalClient,
                               SecurityContext context) {
        this.inventoryClient = inventoryClient;
        this.rentalClient = rentalClient;
        this.context = context;
    }

    @GET
    @Path("all")
    public Uni<List<Reservation>> allReservations() {
        String userId = context.getUserPrincipal() != null ?
                context.getUserPrincipal().getName() : null;
        return Reservation.<Reservation>listAll()
                .onItem().transform(reservations -> reservations.stream()
                        .filter(reservation -> userId == null ||
                                userId.equals(reservation.userId))
                        .collect(Collectors.toList()));
    }

    @Consumes(MediaType.APPLICATION_JSON)
    @POST
    @WithTransaction
    public Uni<Reservation> make(Reservation reservation) {
        reservation.userId = context.getUserPrincipal() != null ?
                context.getUserPrincipal().getName() : "anonymous";
        return reservation.<Reservation>persist().onItem()
                .call(persistedReservation -> {
                    Log.info("Successfully reserved reservation "
                            + persistedReservation);
                    if (persistedReservation.startDay.equals(LocalDate.now()) &&
                            persistedReservation.userId != null) {
                        return rentalClient
                                .start(persistedReservation.userId,
                                        persistedReservation.id)
                                .onItem().invoke(a -> Log.info("createn success " + a))
                                .replaceWithVoid();
                    }
                    return Uni.createFrom().item(persistedReservation);
                });
    }

    @GET
    @Path("availability")
    public Uni<Collection<Car>> availability(@RestQuery LocalDate startDate,
                                 @RestQuery LocalDate endDate) {
        final Map<Long, Car> carsById = new HashMap<>();

        return Reservation.<Reservation>listAll()
                .onItem().transform(reservations -> {
                    for (Reservation reservation : reservations) {
                        if (reservation.isReserved(startDate, endDate)) {
                            carsById.remove(reservation.carId);
                        }
                    }
                    return carsById.values();
                });
    }

    @DELETE
    @WithTransaction
    public Uni<Void> removeReserve() {
        final String userId = context.getUserPrincipal() != null ? context.getUserPrincipal().getName() : "anonimo";
        return Reservation.delete("userId", userId).replaceWithVoid();
    }
}
