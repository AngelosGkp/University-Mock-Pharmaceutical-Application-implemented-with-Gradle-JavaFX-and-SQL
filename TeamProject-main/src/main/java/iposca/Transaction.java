package iposca;

import javafx.beans.property.*;

public class Transaction {
    private final StringProperty time;
    private final StringProperty customer;
    private final StringProperty items;
    private final DoubleProperty amount;
    private final StringProperty paymentType;
    private final StringProperty status;

    public Transaction(String time, String customer, String items, double amount, String paymentType, String status) {
        this.time = new SimpleStringProperty(time);
        this.customer = new SimpleStringProperty(customer);
        this.items = new SimpleStringProperty(items);
        this.amount = new SimpleDoubleProperty(amount);
        this.paymentType = new SimpleStringProperty(paymentType);
        this.status = new SimpleStringProperty(status);
    }

    public String getTime() { return time.get(); }
    public String getCustomer() { return customer.get(); }
    public String getItems() { return items.get(); }
    public double getAmount() { return amount.get(); }
    public String getPaymentType() { return paymentType.get(); }
    public String getStatus() { return status.get(); }
}