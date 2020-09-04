package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class CustomerCardController {


    @FXML
    private AnchorPane mainRoot;

    @FXML
    private Label customerIdCardLabel;

    @FXML
    private Label customerNameCardLabel;

    public AnchorPane getMainRoot() {
        return mainRoot;
    }


    public void setCustomerNameCardLabelText(String name) {
        this.customerNameCardLabel.setText(name);
    }

    public void setCustomerIdCardLabelText(String Id) {
        this.customerIdCardLabel.setText(Id);
    }
}
