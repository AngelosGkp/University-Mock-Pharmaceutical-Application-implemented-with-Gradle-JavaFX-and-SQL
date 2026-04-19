package iposca;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Product {
    private final SimpleStringProperty productName;
    private final SimpleDoubleProperty price;
    private final SimpleIntegerProperty stock;
    private final SimpleIntegerProperty qty;
    private final SimpleIntegerProperty threshold;
    private final SimpleStringProperty supplier;
    private final SimpleIntegerProperty packSize;

    public Product(String name, double price, int stock, int threshold, String supplier, int packSize) {
        this.productName = new SimpleStringProperty(name);
        this.price = new SimpleDoubleProperty(price);
        this.stock = new SimpleIntegerProperty(stock);
        this.qty = new SimpleIntegerProperty(1);
        this.threshold = new SimpleIntegerProperty(threshold);
        this.supplier = new SimpleStringProperty(supplier);
        this.packSize = new SimpleIntegerProperty(packSize);
    }

    public Product(String name, double price, int stock, int threshold) { this(name, price, stock, threshold, "Unknown", 1); }
    public Product(String name, double price, int stock) {this(name, price, stock, 0);} //default = 0


    //getters
    public String getProductName() { return productName.get(); }
    public double getPrice() { return price.get(); }
    public int getStock() { return stock.get(); }
    public int getQty() { return qty.get(); }
    public int getThreshold() { return threshold.get(); }
    public int getPackSize () { return packSize.get(); }
    public String getSupplier () { return supplier.get(); }

    //setters
    public void setQty(int qty) { this.qty.set(qty); }
    public void setPrice(double price) { this.price.set(price); }
    public void setStock(int stock) { this.stock.set(stock); }
    public void setThreshold(int threshold) { this.threshold.set(threshold); }
    public void setSupplier(String supplier) { this.supplier.set(supplier); }
    public void setPackSize(int packSize) { this.packSize.set(packSize); }

    @Override
    public String toString() {
        return getProductName();
    }
}