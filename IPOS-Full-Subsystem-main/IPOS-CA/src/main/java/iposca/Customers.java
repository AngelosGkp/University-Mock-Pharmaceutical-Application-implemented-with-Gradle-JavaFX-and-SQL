package iposca;

import javafx.beans.property.*;

public class Customers {

    private final StringProperty name;
    private final DoubleProperty creditLimit;
    private final DoubleProperty balance;
    private final StringProperty status;
    private final StringProperty dueDate;
    private final StringProperty reminderSent;

    public Customers(String name, Double creditLimit, Double balance, String status, String dueDate, String reminderSent) {
        this.name = new SimpleStringProperty(name);
        this.creditLimit = new SimpleDoubleProperty(creditLimit);
        this.balance = new SimpleDoubleProperty(balance);
        this.status = new SimpleStringProperty(status);
        this.dueDate = new SimpleStringProperty(dueDate);
        this.reminderSent = new SimpleStringProperty(reminderSent);
    }

    public String getName() { return name.get(); }
    public double getLimit() { return creditLimit.get(); }
    public double getBalance() { return balance.get(); }
    public String getStatus() { return status.get(); }
    public String getDueDate() { return dueDate.get(); }
    public String getReminderSent() { return reminderSent.get(); }

    public void setLimit(double limit) { this.creditLimit.set(limit); }
    public void setBalance(double balance) { this.balance.set(balance); }
    public void setStatus(String status) { this.status.set(status); }
}
