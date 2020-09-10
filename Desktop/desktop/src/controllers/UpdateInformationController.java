package controllers;

import SDMImprovedFacade.Store;
import SDMImprovedFacade.StoreItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class UpdateInformationController {

    private final String ADD_ITEM_TO_STORE = "Add Item To Store";
    private final String REMOVE_ITEM_FROM_STORE = "Remove Item From Store";
    private final String UPDATE_ITEM_PRICE = "Update Item Price In Store";

    AppController mainController;

    @FXML
    private AnchorPane mainRoot;

    @FXML
    private ComboBox<String> chooseOperationComboBox;

    @FXML
    private ComboBox<Store> chooseStoreComboBox;

    @FXML
    private ComboBox<StoreItem> chooseItemComboBox;

    @FXML
    private TextField newPriceForItemTextField;

    @FXML
    private Button applyButton;

    public void initializeChooseStoreComboBox() {
        ObservableList<Store> storesObservableList = FXCollections.observableArrayList();
        storesObservableList.addAll(this.mainController.getSDMLogic().getStores().values());
        this.chooseStoreComboBox.setItems(storesObservableList);
    }

    public void initializeChooseOperationComboBox() {
        ObservableList<String> operationsObservableList = FXCollections.observableArrayList();
        operationsObservableList.addAll(ADD_ITEM_TO_STORE, REMOVE_ITEM_FROM_STORE, UPDATE_ITEM_PRICE);
        this.chooseOperationComboBox.setItems(operationsObservableList);
    }

    public void initializeChooseItemComboBox() {
        ObservableList<StoreItem> storeItemsObservableList = FXCollections.observableArrayList();
        storeItemsObservableList.addAll(this.mainController.getSDMLogic().getItems().values());
        this.chooseItemComboBox.setItems(storeItemsObservableList);
    }

    @FXML
    void onActionApplyButton() {
        if (this.chooseOperationComboBox.getSelectionModel().getSelectedItem() != null) {
            if (this.chooseStoreComboBox.getSelectionModel().getSelectedItem() != null) {
                if (this.chooseItemComboBox.getSelectionModel().getSelectedItem() != null) {
                    String operation = this.chooseOperationComboBox.getSelectionModel().getSelectedItem();
                    StoreItem sItem = this.chooseItemComboBox.getSelectionModel().getSelectedItem();
                    Store store = this.chooseStoreComboBox.getSelectionModel().getSelectedItem();

                    switch (operation) {
                        case ADD_ITEM_TO_STORE:
                            addItemToStore(sItem, store);
                            break;
                        case REMOVE_ITEM_FROM_STORE:
                            removeItemFromStore(sItem, store);
                            break;
                        case UPDATE_ITEM_PRICE:
                            updateStoreItemPrice(sItem, store);
                            break;
                    }
                } else {
                    displayUpdateInformationError("Please choose an item first");
                }
            } else {
                displayUpdateInformationError("Please choose a store first");
            }
        } else {
            displayUpdateInformationError("Please choose an action first");
        }
    }

    private void updateStoreItemPrice(StoreItem sItem, Store store) {
        try {
            if(store.getItemsBeingSold().containsKey(sItem.getId())) {
                String newPriceString = this.newPriceForItemTextField.getText();
                if (isNumber(newPriceString)) {
                    if (Double.parseDouble(newPriceString) > 0) {
                        this.newPriceForItemTextField.setStyle("-fx-border-color: none;");
                        this.newPriceForItemTextField.setPromptText("New Price");
                        this.mainController.getSDMLogic().updatePriceOfAnItem(store.getId(), sItem.getId(), Double.parseDouble(newPriceString));
                    } else {
                        displayVisualInvalidInput("are you serious?");
                    }
                } else {
                    displayVisualInvalidInput("no can do bro¯\\_(ツ)_/¯");
                }
            }
            else{ displayUpdateInformationError("You can not update an item that is not being sold by the store"); }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeItemFromStore(StoreItem sItem, Store store) {
        boolean isDiscountDeletedFlag = false;
        if (store.getItemsBeingSold().containsKey(sItem.getId())) {
            if (store.getItemsBeingSold().size() != 1) {
                if (sItem.getAmountOfStoresSellingThisItem() != 1) {
                    this.mainController.getSDMLogic().removeItemFromStore(sItem, store);

                    if (store.getStoreDiscounts().get(sItem.getId()) != null && store.getStoreDiscounts().get(sItem.getId()).size() > 0) {
                        store.getStoreDiscounts().get(sItem.getId()).clear();
                        isDiscountDeletedFlag = true;
                    }

                    if (isDiscountDeletedFlag) {
                        displayOperationSuccessAlert(String.format("The item '%s' was removed successfully from %s along with the discount related to it",
                                sItem.getName(), store.getName()));
                    } else {
                        displayOperationSuccessAlert(String.format("The item '%s' was removed successfully from %s",
                                sItem.getName(), store.getName()));
                    }
                } else {
                    displayUpdateInformationError("You can not delete an item that is being sold by only one store");
                }
            } else {
                displayUpdateInformationError("You can not delete the last item of this store");
            }
        } else {
            displayUpdateInformationError("You can not remove an item that is not being sold by the store");
        }
    }

    private void displayOperationSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Operation Successful");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void addItemToStore(StoreItem sItem, Store store) {
        if (!store.getItemsBeingSold().containsKey(sItem.getId())) {
            this.mainController.getSDMLogic().addItemToStore(sItem, store);
            displayOperationSuccessAlert(String.format("The item '%s' was added successfully to '%s'", sItem.getName(), store.getName()));
        } else {
            displayUpdateInformationError("You can not add an item to a store that is already selling it");
        }
    }

    private void displayUpdateInformationError(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Missing Information Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    @FXML
    void onActionChooseOperation() {
        try {
            String operation = this.chooseOperationComboBox.getSelectionModel().getSelectedItem();
            if(operation != null){
                if(operation.equals(UPDATE_ITEM_PRICE)){
                    this.newPriceForItemTextField.setVisible(true);
                    this.newPriceForItemTextField.setDisable(false);
                }
                else{
                    this.newPriceForItemTextField.setVisible(false);
                    this.newPriceForItemTextField.setDisable(true);
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void loadItemsToSelectItemsComboBox(Collection<StoreItem> storeItems) {
        ObservableList<StoreItem> storeItemsObservableList = FXCollections.observableArrayList();
        storeItemsObservableList.addAll(storeItems);
        this.chooseItemComboBox.setItems(storeItemsObservableList);
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    private boolean isNumber(String numberToCheck) {
        try {
            Double.parseDouble(numberToCheck);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private void displayVisualInvalidInput(String promptText) {
        this.newPriceForItemTextField.setStyle("-fx-border-color: red;");
        this.newPriceForItemTextField.setText("");
        this.newPriceForItemTextField.setPromptText(promptText);
    }

    public AnchorPane getMainRoot() {
        return mainRoot;
    }

}
