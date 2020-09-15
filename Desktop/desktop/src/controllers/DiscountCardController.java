package controllers;

import SDMImprovedFacade.Discount;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class DiscountCardController {
    Discount discount;

    @FXML
    private AnchorPane mainRoot;

    @FXML
    private Label discountCardDiscountNameLabel;

    @FXML
    private Label discountCardStoreId;

    @FXML
    private Label discountCardItemToBuyId;

    @FXML
    private Label discountCardItemToBuyName;

    @FXML
    private Label discountCardStoreNameLabel;

    public AnchorPane getMainRoot() {
        return mainRoot;
    }

    public void setDiscountNameLabel(String discountName){
        discountCardDiscountNameLabel.setText(discountName);
    }

    public void setStoreId(int discountStoreId) {
        this.discountCardStoreId.setText(Integer.toString(discountStoreId));
    }

    public void setDiscountCardItemToBuyId(int itemToBuyId) {
        this.discountCardItemToBuyId.setText(Integer.toString(itemToBuyId));
    }

    public void setDiscountCardItemToBuyName(String discountCardItemToBuyName) {
        this.discountCardItemToBuyName.setText(discountCardItemToBuyName);
    }

    public int getStoreIdOfDiscount() {
        return Integer.parseInt(discountCardStoreId.getText());
    }

    public String getDiscountName() {
        return this.discountCardDiscountNameLabel.getText();
    }

    public void setStoreName(String name) {
        this.discountCardStoreNameLabel.setText(name);
    }
}
