package iposca.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OnlineOrder {
    private int onlineOrderID;
    private String orderReference;
    private LocalDateTime orderDate;
    private String customerEmail;
    private String deliveryAddress;
    private String itemsJson; // e.g. 10000001:3 or 10000002:5
    private BigDecimal totalAmount;
    private String paymentStatus;     // pending, paid, failed
    private String fulfillmentStatus; // received, ready for shipment, dispatched, delivered, etc
    private boolean stockDeducted;
    private LocalDateTime processedAt;
    private Integer processedBy;

    public OnlineOrder() {}

    public int getOnlineOrderID() {
        return onlineOrderID;
    }
    public void setOnlineOrderID(int onlineOrderID) {
        this.onlineOrderID = onlineOrderID;
    }

    public String getOrderReference() {
        return orderReference;
    }
    public void setOrderReference(String orderReference) {
        this.orderReference = orderReference;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }
    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }
    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getItemsJson() {
        return itemsJson;
    }
    public void setItemsJson(String itemsJson) {
        this.itemsJson = itemsJson;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getFulfillmentStatus() {
        return fulfillmentStatus;
    }
    public void setFulfillmentStatus(String fulfillmentStatus) {
        this.fulfillmentStatus = fulfillmentStatus;
    }

    public boolean isStockDeducted() {
        return stockDeducted;
    }
    public void setStockDeducted(boolean stockDeducted) {
        this.stockDeducted = stockDeducted;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }
    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public Integer getProcessedBy() {
        return processedBy;
    }
    public void setProcessedBy(Integer processedBy) {
        this.processedBy = processedBy;
    }
}