package iposca.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AccountHolder {

    private String accountID;
    private String fullName;
    private String address;
    private String phone;
    private String email;
    private BigDecimal creditLimit;
    private BigDecimal currentBalance;
    private String accountStatus;
    private Integer discountPlanID;
    private String status1stReminder;
    private String status2ndReminder;
    private LocalDate date1stReminder;
    private LocalDate date2ndReminder;

    public AccountHolder() {}

    public String getAccountID() {
        return accountID;
    }
    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getFullName() {
        return fullName;
    }
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getCreditLimit() {
        return creditLimit;
    }
    public void setCreditLimit(BigDecimal creditLimit) {
        this.creditLimit = creditLimit;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getAccountStatus() {
        return accountStatus;
    }
    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public Integer getDiscountPlanID() {
        return discountPlanID;
    }
    public void setDiscountPlanID(Integer discountPlanID) {
        this.discountPlanID = discountPlanID;
    }

    public String getStatus1stReminder() {
        return status1stReminder;
    }
    public void setStatus1stReminder(String status) {
        this.status1stReminder = status;
    }

    public String getStatus2ndReminder() {
        return status2ndReminder;
    }
    public void setStatus2ndReminder(String status) {
        this.status2ndReminder = status;
    }

    public LocalDate getDate1stReminder() {
        return date1stReminder;
    }
    public void setDate1stReminder(LocalDate date) {
        this.date1stReminder = date;
    }

    public LocalDate getDate2ndReminder() {
        return date2ndReminder;
    }
    public void setDate2ndReminder(LocalDate date) {
        this.date2ndReminder = date;
    }
}
