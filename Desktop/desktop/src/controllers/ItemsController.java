package controllers;

import SDMImprovedFacade.StoreItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ItemsController {
    AppController mainController;

    @FXML
    private AnchorPane mainRoot;

    @FXML
    private TableView<StoreItem> systemItemsTableView;

    @FXML
    private TableColumn<StoreItem, Integer> systemItemsIdColumn;

    @FXML
    private TableColumn<StoreItem, String> systemItemsNameColumn;

    @FXML
    private TableColumn<StoreItem, String> systemItemsCategoryColumn;

    @FXML
    private TableColumn<StoreItem, Integer> systemItemsAmountStoresSeliingColumn;

    @FXML
    private TableColumn<StoreItem, Double> systemItemsAveragePriceColumn;

    @FXML
    private TableColumn<StoreItem, Double> systemItemsQuantitySoldColumn;

    @FXML
    private Label amountItemsInSystemLabel;

    @FXML
    private void initialize() {
        setSystemItemsTableColumnProperties();

    }

    public AnchorPane getRoot() {
        return mainRoot;
    }

    public void setMainController(AppController appController) {
        mainController = appController;
    }

    private void setSystemItemsTableColumnProperties() {
        this.systemItemsIdColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Integer>("Id"));
        this.systemItemsAmountStoresSeliingColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Integer>("amountOfStoresSellingThisItem"));
        this.systemItemsAveragePriceColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Double>("averagePriceOfTheItem"));
        this.systemItemsCategoryColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, String>("purchaseCategory"));
        this.systemItemsNameColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, String>("name"));
        this.systemItemsQuantitySoldColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Double>("totalItemsSold"));
    }

    public void updateSystemItemsScene(ArrayList<StoreItem> storeItems) {
        try {
            this.systemItemsTableView.getItems().clear();
            ObservableList<StoreItem> observableSystemItemsList = FXCollections.observableArrayList();
            observableSystemItemsList.addAll(storeItems);
            systemItemsTableView.setItems(observableSystemItemsList);
            this.amountItemsInSystemLabel.setText(Integer.toString(storeItems.size()));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
}
