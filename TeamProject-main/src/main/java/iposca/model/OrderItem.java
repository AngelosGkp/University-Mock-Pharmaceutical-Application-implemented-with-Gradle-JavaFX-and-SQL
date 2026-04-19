package iposca.model;

import java.math.BigDecimal;

public class OrderItem {
    private int itemID;
    private int orderID;
    private String productID;
    private int quantity;
    private BigDecimal unitCost;
    private BigDecimal lineTotal;

    public OrderItem() {}

    public OrderItem(int orderID, String productID, int quantity, BigDecimal unitCost) {
        this.orderID = orderID;
        this.productID = productID;
        this.quantity = quantity;
        this.unitCost = unitCost;
        this.lineTotal = unitCost.multiply(BigDecimal.valueOf(quantity));
    }

    public int getItemID() {
        return itemID;
    }
    public void setItemID(int itemID) {
        this.itemID = itemID;
    }

    public int getOrderID() {
        return orderID;
    }
    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public String getProductID() {
        return productID;
    }
    public void setProductID(String productId) {
        this.productID = productID;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitCost() {
        return unitCost;
    }
    public void setUnitCost(BigDecimal unitCost) {
        this.unitCost = unitCost;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }
    public void setLineTotal(BigDecimal lineTotal) {
        this.lineTotal = lineTotal;
    }
}