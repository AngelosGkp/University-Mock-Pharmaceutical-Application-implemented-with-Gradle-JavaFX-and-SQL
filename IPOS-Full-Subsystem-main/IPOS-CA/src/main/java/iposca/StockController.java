package iposca;

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

public class StockController {

    @FXML private TextField searchField;
    @FXML private Label lowStockLabel;
    @FXML private TableView<Product> stockTable;
    @FXML private TableColumn<Product, String> colName;
    @FXML private TableColumn<Product, Integer> colQuantity;
    @FXML private TableColumn<Product, Integer> colThreshold;
    @FXML private TableColumn<Product, String> colStatus;
    private ObservableList<Product> stockList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        stockList.addAll(
                new Product("Paracetamol", 2.50, 100, 10),
                new Product("Ibuprofen", 3.00, 5, 10),
                new Product("Amoxicillin", 12.00, 50, 15),
                new Product("Vitamin C", 1.50, 3, 5)
        );

        if (colName != null) {
            colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        }

        if (colQuantity != null) {
            colQuantity.setCellValueFactory(new PropertyValueFactory<>("stock"));
        }

        if (colThreshold != null) {
            colThreshold.setCellValueFactory(new PropertyValueFactory<>("threshold"));
        }

        if (colStatus != null) {
            colStatus.setCellValueFactory(cellData -> {
                Product p = cellData.getValue();
                if (p.getStock() <= p.getThreshold()) {
                    return new SimpleStringProperty("LOW STOCK");
                } else {
                    return new SimpleStringProperty("OK");
                }
            });
        }

        FilteredList<Product> filteredData = new FilteredList<>(stockList, p -> true);

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(product -> { //threshold check, is current stock less than or equal to the threshold?
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String searchKeyword = newValue.toLowerCase();
                    return product.getProductName().toLowerCase().contains(searchKeyword);
                });
            });
        }

        if (stockTable != null) {
            stockTable.setItems(filteredData);
        }

        updateLowStockWarnings();
    }

    public void updateLowStockWarnings() {
        java.util.List<String> lowStockItems = new java.util.ArrayList<>();

        for (Product product : stockList) {
            if (product.getStock() <= product.getThreshold()) {
                lowStockItems.add(String.format("%s (%d remaining)", product.getProductName(), product.getStock()));
            }
        }

        //colouring
        if (lowStockItems.isEmpty()) {
            lowStockLabel.setText("All stock levels are healthy.");
            lowStockLabel.setStyle("-fx-text-fill: green;");
        } else {
            String message = String.join(", ", lowStockItems); //jointing the list
            lowStockLabel.setText(message);
            lowStockLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        }
    }

    @FXML
    void handleSetThreshold() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Set Low Stock Threshold");
        dialog.setHeaderText("Please choose a product and input its new low stock warning threshold");

        ComboBox<Product> productComboBox = new ComboBox<>(stockList);
        productComboBox.setPromptText("Select a product");
        productComboBox.setPrefWidth(200);

        TextField thresholdField = new TextField();
        thresholdField.setPromptText("f.e, 10");

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Product:"), 0, 0);
        grid.add(productComboBox, 1, 0);
        grid.add(new Label("New Threshold:"), 0, 1);
        grid.add(thresholdField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        javafx.scene.control.Button saveButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(saveButtonType);

        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            Product selectedProduct = productComboBox.getValue();
            String thresholdText = thresholdField.getText();

            if (selectedProduct == null || thresholdText.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setContentText("Please select a product and enter a threshold.");
                alert.showAndWait();
                event.consume(); //stops dialog from closing completely
                return;
            }

            try {
                Integer.parseInt(thresholdText);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setContentText("Please enter a valid whole number for the threshold.");
                alert.showAndWait();
                event.consume();
            }
        });

        dialog.showAndWait().ifPresent(response -> {
            if (response == saveButtonType) {
                Product selectedProduct = productComboBox.getValue();
                int newThreshold = Integer.parseInt(thresholdField.getText());
                selectedProduct.setThreshold(newThreshold);
                stockTable.refresh();
            }
        });
    }

   @FXML
    void handleAddStock() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add New Product");
        dialog.setHeaderText("Please enter the details for the new product");

        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextField stockField = new TextField();
        TextField thresholdField = new TextField();

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Product Name:"), 0, 0);
        grid.add(nameField, 1, 0);

        grid.add(new Label("Price (£):"), 0, 1);
        grid.add(priceField, 1, 1);

        grid.add(new Label("Initial Stock:"), 0, 2);
        grid.add(stockField, 1, 2);

        grid.add(new Label("Low Stock Threshold:"), 0, 3);
        grid.add(thresholdField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        javafx.scene.control.Button saveButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(saveButtonType);

        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            String name = nameField.getText().trim();
            String price = priceField.getText().trim();
            String stock = stockField.getText().trim();
            String threshold = thresholdField.getText().trim();

            if (name.isEmpty() || price.isEmpty() || stock.isEmpty() || threshold.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing Fields");
                alert.setContentText("Please fill out all fields before saving.");
                alert.showAndWait();
                event.consume();
                return;
            }

            try {
                Double.parseDouble(price);
                Integer.parseInt(stock);
                Integer.parseInt(threshold);

            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setContentText("Please ensure price, stock, and threshold are valid numbers and try again");
                alert.showAndWait();
                event.consume();
            }
        });

        dialog.showAndWait().ifPresent(response -> {
            if (response == saveButtonType) {
                String name = nameField.getText().trim();
                double priceDb = Double.parseDouble(priceField.getText().trim());
                int stockInt = Integer.parseInt(stockField.getText().trim());
                int thresholdInt = Integer.parseInt(thresholdField.getText().trim());
                Product newProduct = new Product(name, priceDb, stockInt, thresholdInt);
                stockList.add(newProduct);
            }
        });
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
    public void orders(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/Orders.fxml", "Orders");
    }

    @FXML
    public void customers(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/Customers.fxml", "Account Holders");
    }
}