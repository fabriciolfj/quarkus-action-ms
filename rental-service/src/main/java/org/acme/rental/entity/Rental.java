package org.acme.rental.entity;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import org.bson.codecs.pojo.annotations.BsonProperty;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@MongoEntity(collection = "rentals")
public class Rental extends PanacheMongoEntity {

    public String userId;
    public Long reservationId;
    public LocalDate startDate;
    @BsonProperty("rental-active")
    public boolean active;
    public LocalDate endDate;

    public static Optional<Rental> findByUserAndReservationIdsOptional(String userId, Long reservationId) {
        return find("userId = ?1 and reservationId = ?2", userId, reservationId)
                .firstResultOptional();
    }

    public static List<Rental> listActive() {
        return list("active", true);
    }
}
