package iposca;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;

public class OrdersController {

    @FXML private TextField searchField;
    @FXML private TextField trackOrderField;
    @FXML private javafx.scene.control.Label trackingInfoDisplay;

    @FXML private TableView<Product> supplierTable;
    @FXML private TableColumn<Product, String> colProductName;
    @FXML private TableColumn<Product, String> colSupplier;
    @FXML private TableColumn<Product, Double> colUnitCost;
    @FXML private TableColumn<Product, Integer> colPackSize;

    @FXML private TableView<Product> cartTable;
    @FXML private TableColumn<Product, String> colCartProductName;
    @FXML private TableColumn<Product, Integer> colCartQty;
    @FXML private TableColumn<Product, Double> colCartCost;
    @FXML private TableColumn<Product, Double> colCartLineTotal;

    @FXML private TableView<Order> historyTable;
    @FXML private TableColumn<Order, String> colHistId;
    @FXML private TableColumn<Order, String> colHistDate;
    @FXML private TableColumn<Order, String> colHistStatus;
    @FXML private TableColumn<Order, Double> colHistTotal;

    private final ObservableList<Product> catalogueList = FXCollections.observableArrayList();
    private final ObservableList<Product> cartList = FXCollections.observableArrayList();
    private final ObservableList<Order> historyList = FXCollections.observableArrayList();


    @FXML private void initialize() {
        catalogueList.addAll(
        new Product("Paracetamol 500mg", 2.50, 100, 10, "TestSupplier1", 50),
        new Product("Ibuprofen 200mg", 3.99, 50, 5, "TestSupplier2", 100),
        new Product("Amoxicillin", 15.00, 20, 10, "TestSupplier3", 100),
        new Product("Ibuprofen 400mg", 5.99, 20, 15, "TestSupplier2", 10)
        );

        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colSupplier.setCellValueFactory(new PropertyValueFactory<>("supplier"));
        colUnitCost.setCellValueFactory(new PropertyValueFactory<>("price"));
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));

        colCartProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCartQty.setCellValueFactory(new PropertyValueFactory<>("qty"));
        colCartCost.setCellValueFactory(new PropertyValueFactory<>("price"));

        historyList.addAll(
        new Order("ORD-101", "2026-04-01", "Delivered", 150.50, "Arrived April 3rd"),
        new Order("ORD-102", "2026-04-05", "Pending", 85.00, "Expected April 8th"),
        new Order("ORD-103", "2026-04-06", "Processing", 320.99, "Expected April 10th")
        );

        //calculation for line total using qty * unit cost
        colCartLineTotal.setCellValueFactory(cellData -> {
            Product p = cellData.getValue();
            return new SimpleDoubleProperty(p.getQty() * p.getPrice()).asObject();
        });

        FilteredList<Product> filteredData = new FilteredList<>(catalogueList, p -> true);
        supplierTable.setItems(filteredData);
        cartTable.setItems(cartList);

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(product -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String searchKeyword = newValue.toLowerCase();
                    return product.getProductName().toLowerCase().contains(searchKeyword);
                });
            });
        }

        colHistId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colHistDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colHistStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colHistTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        historyTable.setItems(historyList);
    }

    @FXML
    public void handleAddToCart() {
        Product selected = supplierTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Items Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an item to add to the cart and try again");
            alert.showAndWait();
            return;
        }

        Product existingInCart = cartList.stream()
                .filter(p -> p.getProductName().equals(selected.getProductName()))
                .findFirst()
                .orElse(null);

        if (existingInCart != null) {
            existingInCart.setQty(existingInCart.getQty() + 1);
            cartTable.refresh();
        } else {
            Product cartItem = new Product(
                    selected.getProductName(),
                    selected.getPrice(),
                    selected.getStock(),
                    selected.getThreshold(),
                    selected.getSupplier(),
                    selected.getPackSize()
            );
            cartItem.setQty(1);
            cartList.add(cartItem);
        }
    }

    @FXML
    public void handleRemoveSelected() {
        Product selectedProduct = cartTable.getSelectionModel().getSelectedItem();

        if (selectedProduct == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Items Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an item to remove and try again");
            alert.showAndWait();
            return;
        }
        cartList.remove(selectedProduct);
    }

    @FXML
    public void handleCompleteOrder() {
        if (cartList.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Empty Cart");
            alert.setHeaderText(null);
            alert.setContentText("Your cart is empty. Please add items before completing the order.");
            alert.showAndWait();
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Order");
        confirm.setHeaderText("Place Supplier Order?");
        confirm.setContentText("Confirm?");

        java.util.Optional<javafx.scene.control.ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
            cartList.clear();
            Alert success = new Alert(Alert.AlertType.INFORMATION);
            success.setTitle("Order Successful");
            success.setHeaderText(null);
            success.setContentText("Your order has been sent to the suppliers successfully");
            success.showAndWait();
        }
    }

    @FXML
    public void handleTrackOrder() {
        String inputId = trackOrderField.getText().trim();

        if (inputId.isEmpty()) {
            Alert success = new Alert(Alert.AlertType.WARNING);
            success.setTitle("No Order ID");
            success.setHeaderText(null);
            success.setContentText("No Order ID typed, please try again");
            success.showAndWait();
            return;
        }

        Order foundOrder = historyList.stream()
                .filter(o -> o.getId().equalsIgnoreCase(inputId))
                .findFirst()
                .orElse(null);

        if (foundOrder != null) {
            String info = String.format(
                    "Order ID: %s\nStatus: %s\nExpected Delivery: %s",
                    foundOrder.getId(),
                    foundOrder.getStatus(),
                    foundOrder.getExpectedDelivery()
            );
            trackingInfoDisplay.setText(info);
        } else {
            Alert success = new Alert(Alert.AlertType.WARNING);
            success.setTitle("Couldn't find Order ID");
            success.setHeaderText(null);
            success.setContentText("Couldn't validate Order ID, please try again");
            success.showAndWait();
        }
    }

    @FXML
    public void home(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/loggedIn.fxml", "Logged In");
    }

    @FXML
    public void sales(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/SalesCheckout.fxml", "SalesCheckout");
    }

    @FXML
    public void stock(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/stock.fxml", "Stock");
    }

    @FXML
    public void customers(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/Customers.fxml", "Account Holders");
    }
}
