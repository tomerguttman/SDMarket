package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;

public class loadXMLController {

    @FXML
    private Button buttonChooseFile;

    @FXML
    private Button buttonLoadFile;

    @FXML
    private ProgressBar loadXMLProgressBar;

    @FXML
    private Label pathToFileLabel;

    @FXML
    private GridPane LoadXMLGridView;

    @FXML
    private Label amountCustomersLabel;

    @FXML
    private Label amountItemsLabel;

    @FXML
    private Label amountStoresLabel;

    @FXML
    void onActionChooseXMLFile(ActionEvent event) {
        System.out.println("Choose XML Button was clicked!");
    }

    @FXML
    void onActionLoadXML(ActionEvent event) {

    }

}
