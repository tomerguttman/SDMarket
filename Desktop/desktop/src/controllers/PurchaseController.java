package controllers;

import SDMImprovedFacade.Customer;
import SDMImprovedFacade.Store;
import SDMImprovedFacade.StoreItem;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PurchaseController {
    private AppController mainController;
    private boolean isDynamicPurchaseButtonToggled;
    private SimpleBooleanProperty wasOneOfPurchaseButtonsClicked;

    @FXML
    private AnchorPane mainRoot;

    @FXML
    private ScrollPane storesScrollPane;

    @FXML
    private VBox storeCardsVBox;

    @FXML
    private Label totalDeliveryPriceLabel;

    @FXML
    private Label totalCartPriceLabel;

    @FXML
    private Label totalItemTypesLabel;

    @FXML
    private Label totalItemsLabel;

    @FXML
    private ComboBox<Customer> customerComboBox;

    @FXML
    private Button staticPurchaseButton;

    @FXML
    private Button dynamicPurchaseButton;

    @FXML
    private DatePicker deliveryDatePicker;

    @FXML
    private TableView<StoreItem> purchaseItemsAvailableTableView;

    @FXML
    private TableColumn<StoreItem, String> purchaseItemTableViewItemNameColumn;

    @FXML
    private TableColumn<StoreItem, Double> purchaseItemTableViewPricePerUnitColumn;

    @FXML
    private TableColumn<StoreItem, String> purchaseItemTableViewCategoryColumn;

    @FXML
    private TableColumn<StoreItem, String> purchaseItemTableViewIsAvailableColumn;

    @FXML
    private ComboBox<?> chooseItemToBuyComboBox;

    @FXML
    private TextField itemAmountToBuyTextField;

    @FXML
    private Button addItemToCartButton;

    @FXML
    private TableView<?> shoppingCartTableView;

    @FXML
    private TableColumn<StoreItem, String> shoppingCartTableViewItemNameColumn;

    @FXML
    private TableColumn<StoreItem, Double> shoppingCartTableViewAmountColumn;

    @FXML
    private TableColumn<StoreItem, Double> shoppingCartTableViewTotalPriceColumn;

    @FXML
    private Button buyCartButton;

    @FXML
    private HBox discountsCardsHBox;

    @FXML
    private TableView<?> discountOffersTableView;

    @FXML
    private TableColumn<?, ?> discountOffersTableViewItemNameColumn;

    @FXML
    private TableColumn<?, ?> discountOffersTableViewQuantityColumn;

    @FXML
    private TableColumn<?, ?> discountOffersTableViewForAdditionalColumn;

    @FXML
    private Label discountOperatorLabel;

    @FXML
    private ComboBox<?> chooseOfferComboBox;

    @FXML
    private Button applyDiscountButton;

    @FXML
    private TableView<?> storeFinalOrderDetailsTableView;

    @FXML
    private TableColumn<?, ?> storeFinalOrderDetailsTableViewItemIdColumn;

    @FXML
    private TableColumn<?, ?> storeFinalOrderDetailsTableViewNameColumn;

    @FXML
    private TableColumn<?, ?> storeFinalOrderDetailsTableViewCategoryColumn;

    @FXML
    private TableColumn<?, ?> storeFinalOrderDetailsTableViewQuantityColumn;

    @FXML
    private TableColumn<?, ?> storeFinalOrderDetailsTableViewPricePerUnitColumn;

    @FXML
    private TableColumn<?, ?> storeFinalOrderDetailsTableViewTotalPriceColumn;

    @FXML
    private TableColumn<?, ?> storeFinalOrderDetailsTableViewIsPartOfDiscountColumn;

    @FXML
    private Separator finalOrderDisplaySeparator;

    @FXML
    private ComboBox<?> selectStoreToViewItsOrderDetailsComboBox;

    @FXML
    private HBox acceptRejectOrderHBox;

    @FXML
    private Button acceptOrderButton;

    @FXML
    private Button rejectOrderButton;

    @FXML
    private HBox storeFinalInformationHBox;

    @FXML
    private Label storeFinalInformationHBoxPpkLabel;

    @FXML
    private Label storeFinalInformationHBoxDistanceFromCustomerLabel;

    @FXML
    private Label storeFinalInformationHBoxTotalDeliveryCostLabel;

    @FXML
    private void initialize(){
        this.wasOneOfPurchaseButtonsClicked = new SimpleBooleanProperty(true);
        setPurchaseItemsAvailableTableColumnsProperties();
        setShoppingCartTableColumnsProperties();
        setDiscountOffersTableColumnsProperties();
        setStoreFinalOrderDetailsTableColumnsProperties();
        bindRelevantObjectsToWasOneOfPurchaseButtonsClicked();
    }

    public void insertCustomersToComboBox() {
        ObservableList<Customer> customerObservableList = FXCollections.observableArrayList();
        Collection<Customer> a = this.mainController.getSDMLogic().getCustomers().values();
        customerObservableList.addAll(this.mainController.getSDMLogic().getCustomers().values());
        this.customerComboBox.setItems(customerObservableList);
    }

    private void bindRelevantObjectsToWasOneOfPurchaseButtonsClicked() {
        this.purchaseItemsAvailableTableView.disableProperty().bind(wasOneOfPurchaseButtonsClicked);
        this.discountOffersTableView.disableProperty().bind(wasOneOfPurchaseButtonsClicked);
        this.chooseItemToBuyComboBox.disableProperty().bind(wasOneOfPurchaseButtonsClicked);
        this.itemAmountToBuyTextField.disableProperty().bind(wasOneOfPurchaseButtonsClicked);
        this.addItemToCartButton.disableProperty().bind(wasOneOfPurchaseButtonsClicked);
        this.chooseOfferComboBox.disableProperty().bind(wasOneOfPurchaseButtonsClicked);
        this.applyDiscountButton.disableProperty().bind(wasOneOfPurchaseButtonsClicked);
        this.shoppingCartTableView.disableProperty().bind(wasOneOfPurchaseButtonsClicked);
        this.buyCartButton.disableProperty().bind(wasOneOfPurchaseButtonsClicked);
    }

    @FXML
    void onActionDynamicPurchaseButton() {
        try {
            this.isDynamicPurchaseButtonToggled = true;

            if(wasOneOfPurchaseButtonsClicked.get()) {
                wasOneOfPurchaseButtonsClicked.set(false);
            }

            this.storeCardsVBox.setDisable(true);
            this.purchaseItemsAvailableTableView.getItems().clear();


            ObservableList<StoreItem> observableSystemItemsList = FXCollections.observableArrayList();
            observableSystemItemsList.addAll(this.mainController.getSDMLogic().getItems().values());
            purchaseItemsAvailableTableView.setItems(observableSystemItemsList);

        }
        catch (Exception e) {
            e.printStackTrace();
        }




    }

    @FXML
    void onActionStaticPurchaseButton() {
        this.purchaseItemsAvailableTableView.getItems().clear();
        this.isDynamicPurchaseButtonToggled = false;

        if(wasOneOfPurchaseButtonsClicked.get()) {
            wasOneOfPurchaseButtonsClicked.set(false);
        }

        this.storeCardsVBox.setDisable(false);
    }

    private void setStoreFinalOrderDetailsTableColumnsProperties() {

    }

    private void setDiscountOffersTableColumnsProperties() {
    }

    private void setShoppingCartTableColumnsProperties() {
        this.shoppingCartTableViewItemNameColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, String>("name"));
        this.shoppingCartTableViewAmountColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Double>("totalItemsSold"));
        this.shoppingCartTableViewTotalPriceColumn.setCellValueFactory(cellData -> {
            if(this.isDynamicPurchaseButtonToggled) {
                return new SimpleObjectProperty<Double>(this.mainController.getSDMLogic().getCheapestPriceForItem(cellData.getValue().getId()));
            }
            else { //Static purchase
                return new SimpleObjectProperty<Double>(cellData.getValue().getTotalPrice());
            }
        });
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    private void setPurchaseItemsAvailableTableColumnsProperties() {
        this.purchaseItemTableViewItemNameColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, String>("name"));
        this.purchaseItemTableViewPricePerUnitColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Double>("pricePerUnit"));
        this.purchaseItemTableViewCategoryColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, String>("purchaseCategory"));
        this.purchaseItemTableViewIsAvailableColumn.setCellValueFactory(cellData -> {
            if(isDynamicPurchaseButtonToggled) { return new SimpleObjectProperty<>("True"); }
            else if (cellData.getValue().getPricePerUnit() == 0.0) { return new SimpleObjectProperty<>("False"); }
            else { return new SimpleObjectProperty<>("True"); }
        });
    }

    public AnchorPane getMainRoot() {
        return this.mainRoot;
    }

    public void addStoreCards(Map<Integer, StoreCardController> storeCardControllerMapForPurchase) {
        storeCardsVBox.getChildren().clear();

        for (StoreCardController storeCard : storeCardControllerMapForPurchase.values()) {
            storeCardsVBox.getChildren().add(storeCard.getMainRoot());
            storeCard.getMainRoot().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    AnchorPane storeCard = (AnchorPane) event.getSource(); //?
                    Pane pane = (Pane)storeCard.getChildren().get(0);
                    int storeId = Integer.parseInt(((Label)pane.getChildren().get(1)).textProperty().get());
                    Store store = mainController.getSDMLogic().getStores().get(storeId);
                    updatePurchaseItemAvailableTableView(store);

                }
            });
        }

        storeCardsVBox.setDisable(true);
    }

    private void updatePurchaseItemAvailableTableView(Store store) {
        try {
            Map<Integer, StoreItem> itemsToAdd = new HashMap<>();
            itemsToAdd.putAll(store.getItemsBeingSold());

            for (StoreItem sItem : mainController.getSDMLogic().getItems().values()) {
                if(itemsToAdd.containsKey(sItem.getId())) {
                    itemsToAdd.get(sItem.getId()).setIsAvailable(true);
                }
                else {  itemsToAdd.put(sItem.getId(), sItem); }
            }

            this.purchaseItemsAvailableTableView.getItems().clear();
            ObservableList<StoreItem> observableStoreItemsList = FXCollections.observableArrayList();
            observableStoreItemsList.addAll(itemsToAdd.values());
            purchaseItemsAvailableTableView.setItems(observableStoreItemsList);
        }

        catch(Exception e) {
            e.printStackTrace();
        }
    }

}
