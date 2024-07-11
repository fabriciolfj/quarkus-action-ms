package org.acme.billing;

import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;

@MongoEntity(collection = "invoices")
public class Invoice extends PanacheMongoEntity {
    public double totalPrice;
    public boolean paid;
    public Reservation reservation;

    public Invoice(double price, boolean paid, Reservation reservation) {
        this.totalPrice = price;
        this.paid = paid;
        this.reservation = reservation;
    }

    @Override
    public String toString() {
        return "Invoice{" +
                "totalPrice=" + totalPrice +
                ", paid=" + paid +
                ", reservation=" + reservation +
                '}';
    }
}
