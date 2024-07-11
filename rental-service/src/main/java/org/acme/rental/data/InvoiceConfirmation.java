package org.acme.rental.data;

public class InvoiceConfirmation {
    public Invoice invoice;
    public boolean paid;

    @Override
    public String toString() {
        return "InvoiceConfirmation{" +
                "invoice=" + invoice +
                ", paid=" + paid +
                '}';
    }
}
