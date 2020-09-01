package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class StoresController {
    private AppController mainController;

    @FXML
    private AnchorPane mainRoot;

    @FXML
    private VBox storeCardsVBox;

    @FXML
    private ScrollPane storesScrollPane;

    @FXML
    private TableView<?> itemsTableView;

    @FXML
    private TableView<?> ordersTableView;

    @FXML
    private TableView<?> salesTableView;

    @FXML
    private Label ppkLabel;

    @FXML
    private Label totalRevenueLabel;

    @FXML
    private Label salesLabel;

    @FXML
    private Label itemsLabel;

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public AnchorPane getMainRoot() {
        return mainRoot;
    }

    public void addStoreCards(List<StoreCardController> storeCards) {
        storeCards.forEach(storeCard -> storeCardsVBox.getChildren().add(storeCard.getMainRoot()));
    }
}
