package org.acme.billing;

public class InvoiceConfirmation {

    public Invoice invoice;
    public boolean paid;

    public InvoiceConfirmation(Invoice invoice, boolean paid) {
        this.invoice = invoice;
        this.paid = paid;
    }
}
