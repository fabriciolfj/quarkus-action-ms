package org.acme.rental.data;

import java.time.LocalDate;

public class Reservation {
    public Long id;
    public String userId;
    public LocalDate startDay;

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", startDay=" + startDay +
                '}';
    }
}
