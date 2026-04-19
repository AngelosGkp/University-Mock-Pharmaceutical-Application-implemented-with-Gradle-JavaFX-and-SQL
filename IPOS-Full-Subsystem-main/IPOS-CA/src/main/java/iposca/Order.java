package iposca;

import javafx.beans.property.*;

public class Order {
    private final StringProperty id;
    private final StringProperty date;
    private final StringProperty status;
    private final DoubleProperty total;
    private final StringProperty expectedDelivery;

    public Order(String id, String date, String status, double total, String delivery) {
        this.id = new SimpleStringProperty(id);
        this.date = new SimpleStringProperty(date);
        this.status = new SimpleStringProperty(status);
        this.total = new SimpleDoubleProperty(total);
        this.expectedDelivery = new SimpleStringProperty(delivery);
    }

    //getters
    public String getId() { return id.get(); }
    public String getDate() { return date.get(); }
    public String getStatus() { return status.get(); }
    public double getTotal() { return total.get(); }
    public String getExpectedDelivery() { return expectedDelivery.get(); }
}