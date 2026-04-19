package iposca;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class CreditCardController {

    @FXML private TextField nameField, numberField, cvvField;
    @FXML private DatePicker expiryField;
    @FXML private Label updatedTotal;

    private SalesCheckoutController salesController;

    public void setSalesController(SalesCheckoutController controller) {
        this.salesController = controller;
    }

    public void receiveTotal(String tl) {
        updatedTotal.setText(tl);
    }

    @FXML void handleSubmit(MouseEvent event) {
        //validation
        if (nameField.getText().trim().isEmpty() ||
                numberField.getText().trim().isEmpty() ||
                cvvField.getText().trim().isEmpty() ||
                expiryField.getValue() == null) {

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Field Missing");
            alert.setHeaderText(null);
            alert.setContentText("Please check you have filled all sections and try again");
            alert.showAndWait();
            return;
        }

        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

}