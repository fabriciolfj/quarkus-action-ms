package org.acme.rental.data;

public class Invoice {
    public boolean paid;
    public Reservation reservation;

    @Override
    public String toString() {
        return "Invoice{" +
                "paid=" + paid +
                ", reservation=" + reservation +
                '}';
    }
}
