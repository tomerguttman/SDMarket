package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class StoreCardController {

    @FXML
    private AnchorPane mainRoot;

    @FXML
    private Label storeIdCardLabel;

    @FXML
    private Label storeNameCardLabel;

    public void setStoreIdCardLabelText(String storeId){
        storeIdCardLabel.setText(storeId);
    }

    public void setStoreNameCardLabelText(String storeName){
        storeNameCardLabel.setText(storeName);
    }

    @FXML
    void onMouseClickedStoreCard(MouseEvent event) {

    }

    public AnchorPane getMainRoot() {
        return mainRoot;
    }
}
