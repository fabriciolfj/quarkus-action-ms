package org.acme.billing.data;


public class ReservationInvoice {

    public Reservation reservation;
    public double price;

    public ReservationInvoice(Reservation reservation, double price) {
        this.reservation = reservation;
        this.price = price;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
