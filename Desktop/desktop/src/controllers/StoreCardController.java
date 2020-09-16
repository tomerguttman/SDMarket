package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class StoreCardController {

    @FXML
    private AnchorPane mainRoot;

    @FXML
    private Label storeIdCardLabel;

    @FXML
    private Label storeNameCardLabel;

    @FXML
    private Label storeCardLocationLabel;

    public void setStoreIdCardLabelText(String storeId){
        storeIdCardLabel.setText(storeId);
    }

    public void setStoreNameCardLabelText(String storeName){
        storeNameCardLabel.setText(storeName);
    }

    public AnchorPane getMainRoot() {
        return mainRoot;
    }

    public void setLocationLabelText(String locationString) {
        this.storeCardLocationLabel.setText(locationString);
    }
}
