package iposca.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Reminder {
    private int reminderID;
    private String accountID;
    private String reminderType; // 1st reminder, 2nd reminder
    private BigDecimal amountOwed;
    private String reminderText;
    private LocalDate dateGenerated;
    private LocalDate dateSent;
    private String sentVia; // print or email
    private String status;

    public Reminder() {}

    public Reminder(String accountID, String reminderType, BigDecimal amountOwed,
                    String reminderText, LocalDate dateGenerated) {
        this.accountID = accountID;
        this.reminderType = reminderType;
        this.amountOwed = amountOwed;
        this.reminderText = reminderText;
        this.dateGenerated = dateGenerated;
        this.status = "Generated";
        this.sentVia = "Print";
    }

    public int getReminderID() {
        return reminderID;
    }
    public void setReminderID(int reminderID) {
        this.reminderID = reminderID;
    }

    public String getAccountID() {
        return accountID;
    }
    public void setAccountID(String accountId) {
        this.accountID = accountID;
    }

    public String getReminderType() {
        return reminderType;
    }
    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public BigDecimal getAmountOwed() {
        return amountOwed;
    }
    public void setAmountOwed(BigDecimal amountOwed) {
        this.amountOwed = amountOwed;
    }

    public String getReminderText() {
        return reminderText;
    }
    public void setReminderText(String reminderText) {
        this.reminderText = reminderText;
    }

    public LocalDate getDateGenerated() {
        return dateGenerated;
    }
    public void setDateGenerated(LocalDate dateGenerated) {
        this.dateGenerated = dateGenerated;
    }

    public LocalDate getDateSent() {
        return dateSent;
    }
    public void setDateSent(LocalDate dateSent) {
        this.dateSent = dateSent;
    }

    public String getSentVia() {
        return sentVia;
    }
    public void setSentVia(String sentVia) {
        this.sentVia = sentVia;
    }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}