package org.acme.billing.data;


import java.time.LocalDate;

public class Reservation {

    public Long id;
    public String userId;
    public Long carId;
    public LocalDate startDay;
    public LocalDate endDay;

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", carId=" + carId +
                ", startDay=" + startDay +
                ", endDay=" + endDay +
                '}';
    }
}
