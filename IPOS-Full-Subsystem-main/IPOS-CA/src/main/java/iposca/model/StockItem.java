package iposca.model;

import java.math.BigDecimal;

public class StockItem {
    private String productID;
    private String productName;
    private String description;
    private String unitType;
    private String form;
    private int packSize;
    private BigDecimal wholesaleCost;
    private BigDecimal retailPrice;
    private int currentStock;
    private int reorderLevel;
    private boolean isActive;

    public StockItem() {}

    public String getProductID() {
        return productID;
    }
    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getWholesaleCost() {
        return wholesaleCost;
    }
    public void setWholesaleCost(BigDecimal wholesaleCost) {
        this.wholesaleCost = wholesaleCost;
    }

    public BigDecimal getRetailPrice() {
        return retailPrice;
    }
    public void setRetailPrice(BigDecimal retailPrice) {
        this.retailPrice = retailPrice;
    }

    public int getCurrentStock() {
        return currentStock;
    }
    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getReorderLevel() {
        return reorderLevel;
    }
    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    public boolean isActive() {
        return isActive;
    }
    public void setActive(boolean active) {
        isActive = active;
    }

    public String getUnitType() {
        return unitType;
    }
    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public String getForm() {
        return form;
    }
    public void setForm(String form) {
        this.form = form;
    }

    public int getPackSize() {
        return packSize;
    }
    public void setPackSize(int packSize) {
        this.packSize = packSize;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}