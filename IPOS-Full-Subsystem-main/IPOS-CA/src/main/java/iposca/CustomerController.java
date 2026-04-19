package iposca;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;

public class CustomerController {

    @FXML private TextField searchField;
    @FXML private TableView<Customers> customerTable;
    @FXML private TableColumn<Customers, String> colName;
    @FXML private TableColumn<Customers, Double> colLimit;
    @FXML private TableColumn<Customers, Double> colBalance;
    @FXML private TableColumn<Customers, String> colStatus;

    @FXML private TableView<Customers> overdueTable;
    @FXML private TableColumn<Customers, String> colOverdueName;
    @FXML private TableColumn<Customers, Double> colOverdueAmount;
    @FXML private TableColumn<Customers, String> colOverdueDate;
    @FXML private TableColumn<Customers, String> colOverdueReminder;

    private final ObservableList<Customers> customerList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        customerList.addAll(
                new Customers("Test Customer 1", 500.0, 150.0, "OK", "N/A", "None"),
                new Customers("Test Customer 2", 1000.0, 1200.0, "OVERDUE", "2026-03-15", "1st Reminder"),
                new Customers("Test Customer 3", 2500.0, 0.0, "OK", "N/A", "None"),
                new Customers("Test Customer 4", 800.0, 850.5, "OVERDUE", "2026-03-01", "2nd Reminder")
        );

        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colLimit.setCellValueFactory(new PropertyValueFactory<>("limit"));
        colBalance.setCellValueFactory(new PropertyValueFactory<>("balance"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colOverdueName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colOverdueAmount.setCellValueFactory(new PropertyValueFactory<>("balance"));
        colOverdueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colOverdueReminder.setCellValueFactory(new PropertyValueFactory<>("reminderSent"));

        FilteredList<Customers> overdueData = new FilteredList<>(customerList,
                customer -> customer.getStatus().equals("OVERDUE"));

        overdueTable.setItems(overdueData);

        colStatus.setCellFactory(column -> {
            return new javafx.scene.control.TableCell<Customers, String>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    } else {
                        setText(item);
                        if (item.equals("OK")) {
                            setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
                        } else if (item.equals("OVERDUE")) {
                            setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                        }
                    }
                }
            };
        });

        FilteredList<Customers> filteredData = new FilteredList<>(customerList, p -> true);

        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filteredData.setPredicate(customer -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return customer.getName().toLowerCase().contains(lowerCaseFilter);
                });
            });
        }
        customerTable.setItems(filteredData);
    }

    @FXML
    void handleSetLimit() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Set Credit Limit");
        dialog.setHeaderText("Update a customer's credit limit");

        ComboBox<Customers> customersComboBox = new ComboBox<>(customerList);
        customersComboBox.setPromptText("Select an account holder");
        customersComboBox.setPrefWidth(200);

        customersComboBox.setCellFactory(lv -> new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Customers item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });
        customersComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override protected void updateItem(Customers item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });

        TextField limitField = new TextField();

        javafx.scene.layout.GridPane grid = new javafx.scene.layout.GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        grid.add(new Label("Account Holder:"), 0, 0);
        grid.add(customersComboBox, 1, 0);
        grid.add(new Label("New Limit (£):"), 0, 1);
        grid.add(limitField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        javafx.scene.control.Button saveButton = (javafx.scene.control.Button) dialog.getDialogPane().lookupButton(saveButtonType);

        saveButton.addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            Customers selected = customersComboBox.getValue();
            String limitInput = limitField.getText();

            if (selected == null || limitInput.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Missing Input");
                alert.setContentText("Please select a customer and try again");
                alert.showAndWait();
                return;
            }

            try {
                Double.parseDouble(limitInput);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Number");
                alert.setContentText("Please select a valid number and try again");
                alert.showAndWait();
            }
        });

        dialog.showAndWait().ifPresent(response -> {
            if (response == saveButtonType) {
                Customers selected = customersComboBox.getValue();
                double newLimit = Double.parseDouble(limitField.getText());

                selected.setLimit(newLimit);

                customerTable.refresh();
                overdueTable.refresh();
            }
        });
    }

    @FXML
    void handleAddAccount() {
        Dialog<Customers> dialog = new Dialog<>();
        dialog.setTitle("Add New Account Holder");
        dialog.setHeaderText("Enter the details for the new customer account");

        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField nameField = new TextField();
        TextField limitField = new TextField();

        grid.add(new Label("Customer Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(new Label("Credit Limit (in £):"), 0, 1);
        grid.add(limitField, 1, 1);

        dialog.getDialogPane().setContent(grid);
        javafx.application.Platform.runLater(nameField::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String name = nameField.getText().trim();
                    double limit = Double.parseDouble(limitField.getText().trim());

                    if (name.isEmpty()) {
                        throw new Exception("Name cannot be empty.");
                    }

                    return new Customers(name, limit, 0.0, "OK", "N/A", "None");
                } catch (NumberFormatException e) {
                    showError("Invalid Input", "Please enter a valid number for the credit limit");
                } catch (Exception e) {
                    showError("Invalid Input", e.getMessage());
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(customerList::add);
    }

    // Helper method for error alerts
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void home(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/loggedIn.fxml", "Dashboard");
    }

    @FXML
    void sales(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/SalesCheckout.fxml", "Sales Checkout");
    }

    @FXML
    void stock(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/stock.fxml", "Stock");
    }

    @FXML
    public void orders(MouseEvent event) throws IOException {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Utils.switchScene(stage, "/Orders.fxml", "Orders");
    }
}