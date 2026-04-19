package iposca;

import iposca.model.Order;
import iposca.model.OrderItem;
import iposca.model.StockItem;
import iposca.service.OrderService;
import iposca.service.StockService;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrdersController {

    @FXML private TextField searchField;
    @FXML private TextField trackOrderField;
    @FXML private Label trackingInfoDisplay;

    @FXML private TableView<StockItem> supplierTable;
    @FXML private TableColumn<StockItem, String> colProductName;
    @FXML private TableColumn<StockItem, String> colSupplier;
    @FXML private TableColumn<StockItem, Double> colUnitCost;
    @FXML private TableColumn<StockItem, Integer> colPackSize;

    @FXML private TableView<StockItem> cartTable;
    @FXML private TableColumn<StockItem, String> colCartProductName;
    @FXML private TableColumn<StockItem, Integer> colCartQty;
    @FXML private TableColumn<StockItem, Double> colCartCost;
    @FXML private TableColumn<StockItem, Double> colCartLineTotal;

    @FXML private TableView<iposca.model.Order> historyTable;
    @FXML private TableColumn<iposca.model.Order, String> colHistId;
    @FXML private TableColumn<iposca.model.Order, String> colHistDate;
    @FXML private TableColumn<iposca.model.Order, String> colHistStatus;
    @FXML private TableColumn<iposca.model.Order, Double> colHistTotal;

    private final ObservableList<StockItem> catalogueList = FXCollections.observableArrayList();
    private final ObservableList<StockItem> cartList = FXCollections.observableArrayList();
    private final ObservableList<iposca.model.Order> historyList = FXCollections.observableArrayList();
    private final Map<String, Integer> cartQuantities = new HashMap<>();

    @FXML
    private void initialize() {
        setupCatalogueTable();
        setupCartTable();
        setupHistoryTable();
        loadCatalogue();
        loadOrderHistory();
        setupSearch();
    }

    private void setupCatalogueTable() {
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colSupplier.setCellValueFactory(cellData ->
                new SimpleStringProperty("InfoPharma"));
        colUnitCost.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(
                        cellData.getValue().getWholesaleCost().doubleValue()).asObject());
        colPackSize.setCellValueFactory(new PropertyValueFactory<>("packSize"));
        supplierTable.setItems(catalogueList);
    }

    private void setupCartTable() {
        colCartProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCartQty.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleObjectProperty<>(
                        cartQuantities.getOrDefault(
                                cellData.getValue().getProductID(), 1)));
        colCartCost.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(
                        cellData.getValue().getWholesaleCost().doubleValue()).asObject());
        colCartLineTotal.setCellValueFactory(cellData -> {
            StockItem item = cellData.getValue();
            int qty = cartQuantities.getOrDefault(item.getProductID(), 1);
            return new SimpleDoubleProperty(
                    item.getWholesaleCost().doubleValue() * qty).asObject();
        });
        cartTable.setItems(cartList);
    }

    private void setupHistoryTable() {
        colHistId.setCellValueFactory(new PropertyValueFactory<>("orderReference"));
        colHistDate.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        cellData.getValue().getOrderDate().toLocalDate().toString()));
        colHistStatus.setCellValueFactory(new PropertyValueFactory<>("orderStatus"));
        colHistTotal.setCellValueFactory(cellData ->
                new SimpleDoubleProperty(
                        cellData.getValue().getTotalAmount().doubleValue()).asObject());

        // colour code status column
        colHistStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null); setStyle("");
                } else {
                    setText(status);
                    switch (status) {
                        case "Delivered"   -> setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        case "Dispatched"  -> setStyle("-fx-text-fill: blue;");
                        case "Cancelled"   -> setStyle("-fx-text-fill: red;");
                        default            -> setStyle("-fx-text-fill: orange;");
                    }
                }
            }
        });
        historyTable.setItems(historyList);
    }

    private void loadCatalogue() {
        try {
            List<StockItem> items = StockService.getAllStock();
            catalogueList.setAll(items);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load catalogue: " + e.getMessage());
        }
    }

    private void loadOrderHistory() {
        try {
            List<iposca.model.Order> orders = OrderService.getAllOrders();
            historyList.setAll(orders);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Could not load order history: " + e.getMessage());
        }
    }

    private void setupSearch() {
        FilteredList<StockItem> filteredData = new FilteredList<>(catalogueList, p -> true);
        supplierTable.setItems(filteredData);
        if (searchField != null) {
            searchField.textProperty().addListener((obs, oldVal, newVal) -> {
                filteredData.setPredicate(product -> {
                    if (newVal == null || newVal.isEmpty()) return true;
                    return product.getProductName().toLowerCase()
                            .contains(newVal.toLowerCase());
                });
            });
        }
    }

    @FXML
    public void handleAddToCart() {
        StockItem selected = supplierTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No item selected. Please select an item and try again.");
            return;
        }

        String pid = selected.getProductID();
        if (cartQuantities.containsKey(pid)) {
            cartQuantities.put(pid, cartQuantities.get(pid) + 1);
            cartTable.refresh();
        } else {
            cartQuantities.put(pid, 1);
            cartList.add(selected);
        }
    }

    @FXML
    public void handleRemoveSelected() {
        StockItem selected = cartTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("No item selected. Please select an item to remove.");
            return;
        }
        String pid = selected.getProductID();
        int qty = cartQuantities.getOrDefault(pid, 1);
        if (qty > 1) {
            cartQuantities.put(pid, qty - 1);
            cartTable.refresh();
        } else {
            cartQuantities.remove(pid);
            cartList.remove(selected);
        }
    }

    @FXML
    public void handleCompleteOrder() {
        if (cartList.isEmpty()) {
            showWarning("Your cart is empty. Please add items before completing the order.");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Order");
        confirm.setHeaderText("Place Supplier Order?");
        confirm.setContentText("This will submit the order to InfoPharma. Confirm?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    List<OrderItem> items = new ArrayList<>();
                    for (StockItem item : cartList) {
                        int qty = cartQuantities.getOrDefault(item.getProductID(), 1);
                        OrderItem oi = new OrderItem();
                        oi.setProductID(item.getProductID());
                        oi.setQuantity(qty);
                        oi.setUnitCost(item.getWholesaleCost());
                        oi.setLineTotal(item.getWholesaleCost()
                                .multiply(BigDecimal.valueOf(qty)));
                        items.add(oi);
                    }

                    String ref = OrderService.placeOrder(items);

                    // clear cart
                    cartList.clear();
                    cartQuantities.clear();

                    // reload history so new order appears
                    loadOrderHistory();

                    Alert success = new Alert(Alert.AlertType.INFORMATION);
                    success.setTitle("Order Placed");
                    success.setContentText(
                            "Order submitted successfully.\nReference: " + ref);
                    success.showAndWait();

                } catch (Exception e) {
                    e.printStackTrace();
                    showError("Could not place order: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    public void handleTrackOrder() {
        String inputId = trackOrderField.getText().trim();
        if (inputId.isEmpty()) {
            showWarning("Please enter an Order ID to track.");
            return;
        }

        iposca.model.Order found = historyList.stream()
                .filter(o -> o.getOrderReference().equalsIgnoreCase(inputId))
                .findFirst()
                .orElse(null);

        if (found != null) {
            StringBuilder info = new StringBuilder();
            info.append("Order Reference: ").append(found.getOrderReference()).append("\n");
            info.append("Status: ").append(found.getOrderStatus()).append("\n");
            info.append("Order Date: ").append(
                    found.getOrderDate().toLocalDate()).append("\n");
            if (found.getDispatchDate() != null)
                info.append("Dispatched: ").append(found.getDispatchDate()).append("\n");
            if (found.getDeliveryDate() != null)
                info.append("Delivered: ").append(found.getDeliveryDate()).append("\n");
            if (found.getCourier() != null)
                info.append("Courier: ").append(found.getCourier()).append("\n");
            if (found.getTrackingNumber() != null)
                info.append("Tracking No: ").append(found.getTrackingNumber());
            trackingInfoDisplay.setText(info.toString());
        } else {
            showWarning("No order found with ID: " + inputId);
        }
    }

    // called when a delivery physically arrives, increases local stock
    @FXML
    public void handleConfirmDelivery() {
        iposca.model.Order selected = historyTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showWarning("Please select an order from the history table to confirm delivery.");
            return;
        }
        if (selected.getOrderStatus().equals("Delivered")) {
            showWarning("This order has already been marked as delivered.");
            return;
        }

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Confirm Delivery");
        dialog.setHeaderText("Confirm delivery for order: " + selected.getOrderReference());

        TextField courierField = new TextField();
        courierField.setPromptText("e.g. DHL");
        TextField trackingField = new TextField();
        trackingField.setPromptText("Tracking number");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10); grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        grid.add(new Label("Courier:"), 0, 0);
        grid.add(courierField, 1, 0);
        grid.add(new Label("Tracking No:"), 0, 1);
        grid.add(trackingField, 1, 1);
        dialog.getDialogPane().setContent(grid);

        ButtonType confirmType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmType, ButtonType.CANCEL);

        dialog.showAndWait().ifPresent(response -> {
            if (response == confirmType) {
                try {
                    boolean success = OrderService.confirmDelivery(
                            selected.getOrderID(),
                            courierField.getText().trim(),
                            trackingField.getText().trim()
                    );
                    if (success) {
                        loadOrderHistory();
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Delivery Confirmed");
                        alert.setContentText(
                                "Delivery confirmed. Stock has been updated.");
                        alert.showAndWait();
                    }
                } catch (Exception e) {
                    showError("Could not confirm delivery: " + e.getMessage());
                }
            }
        });
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setContentText(msg); a.showAndWait();
    }

    private void showWarning(String msg) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setContentText(msg); a.showAndWait();
    }

    @FXML public void home(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/loggedIn.fxml", "Logged In");
    }
    @FXML public void sales(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/SalesCheckout.fxml", "SalesCheckout");
    }
    @FXML public void stock(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/stock.fxml", "Stock");
    }
    @FXML public void customers(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/Customers.fxml", "Account Holders");
    }
}