package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class DiscountCardController {
    @FXML
    private AnchorPane mainRoot;

    @FXML
    private Label discountCardDiscountNameLabel;

    public void setDiscountNameLabel(String discountName){
        discountCardDiscountNameLabel.setText(discountName);
    }

    public AnchorPane getMainRoot() {
        return mainRoot;
    }
}
