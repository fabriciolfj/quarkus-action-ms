package org.acme.rental.entity;


import java.time.LocalDate;

public class Reservation {

    public Long id;
    public String userId;
    public Long carId;
    public LocalDate startDay;
    public LocalDate endDay;

    @Override
    public String toString() {
        return super.toString();
    }
}
