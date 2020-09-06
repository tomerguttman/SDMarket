package controllers;

import SDMImprovedFacade.*;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.*;

public class PurchaseController {

    private AppController mainController;
    private final List<DiscountCardController> discountCardControllersList = new ArrayList<>();
    private boolean isDynamicPurchaseButtonToggled;
    private int currentStaticStoreId = -1;
    private SimpleBooleanProperty wasOneOfPurchaseButtonsClicked;
    private final Order currentOrder = new Order();
    private Map<Integer, List<StoreItem>> dynamicOrder;
    private final List<Discount> discountsAvailable = new ArrayList<>();
    private final Map<Integer, Store> storesParticipatingInOrder = new HashMap<>();

    private SimpleDoubleProperty cartPriceProperty = new SimpleDoubleProperty();
    private SimpleDoubleProperty deliveryCostProperty = new SimpleDoubleProperty();
    private SimpleIntegerProperty itemTypesProperty = new SimpleIntegerProperty();
    private SimpleIntegerProperty totalItemsProperty = new SimpleIntegerProperty();

    @FXML
    private AnchorPane mainRoot;

    @FXML
    private ScrollPane storesScrollPane;

    @FXML
    private VBox storeCardsVBox;

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
    private TableColumn<StoreItem, Integer> purchaseItemTableViewItemIdColumn;

    @FXML
    private TableColumn<StoreItem, String> purchaseItemTableViewItemNameColumn;

    @FXML
    private TableColumn<StoreItem, Double> purchaseItemTableViewPricePerUnitColumn;

    @FXML
    private TableColumn<StoreItem, String> purchaseItemTableViewCategoryColumn;

    @FXML
    private TableColumn<StoreItem, String> purchaseItemTableViewIsAvailableColumn;

    @FXML
    private ComboBox<StoreItem> chooseItemToBuyComboBox;

    @FXML
    private TextField itemAmountToBuyTextField;

    @FXML
    private Button addItemToCartButton;

    @FXML
    private TableView<StoreItem> shoppingCartTableView;

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
        setBuyCartEventHandler();

    }

    private void setBuyCartEventHandler() {
        this.buyCartButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                buyCartMethod();
            }
        });
    }

    private void buyCartMethod() {
        if(!shoppingCartTableView.getItems().isEmpty()){
            if(this.isDynamicPurchaseButtonToggled) {
                this.dynamicOrder = initializeDynamicOrderAndAddDiscountCards();
            }
            else {
                initializeStaticOrderAndAddDiscountsCards();
            }
        }
        else { displayEmptyCartError(); }
    }

    private void initializeStaticOrderAndAddDiscountsCards() {
    }

    private Map<Integer, List<StoreItem>> initializeDynamicOrderAndAddDiscountCards() {
        Map<Integer, List<StoreItem>> itemListForEachStoreMap = createItemsListForEachStore();
        addRelevantDiscountCardsToHBox(itemListForEachStoreMap);
        return itemListForEachStoreMap;
    }

    private void addRelevantDiscountCardsToHBox(Map<Integer, List<StoreItem>> itemListForEachStoreMap) {
        this.discountsCardsHBox.getChildren().clear();
        itemListForEachStoreMap.forEach((storeId, itemListForTheStore) -> {
            Store currentStore = this.mainController.getSDMLogic().getStores().get(storeId);
            //There are no duplicates in the input map;
            for (StoreItem sItem : itemListForTheStore ) {
                if(currentStore.getStoreDiscounts() != null) {
                    if(currentStore.getStoreDiscounts().containsKey(sItem.getId())) {
                        try {
                            createAndAddDiscountCards(currentStore.getStoreDiscounts().get(sItem.getId()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        pushDiscountCardsToHBoxAndInitMouseClick();
    }

    private void pushDiscountCardsToHBoxAndInitMouseClick() {
        //this.discountsCardsHBox.getChildren().clear();

        for (DiscountCardController discountCard : this.discountCardControllersList) {
            this.discountsCardsHBox.getChildren().add(discountCard.getMainRoot());
            discountCard.getMainRoot().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) { discountCardClickMethod(event); }
            });
        }

        this.discountsCardsHBox.getChildren();
    }

    private void discountCardClickMethod(MouseEvent event) {
        //DO SOMETHING!!
    }

    private void createAndAddDiscountCards(List<Discount> discountForCurrentItem) throws IOException {
        try {
            for (Discount discount : discountForCurrentItem) {
                DiscountCardController discountCardController = createDiscountController(discount);
                this.discountCardControllersList.add(discountCardController);
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private DiscountCardController createDiscountController(Discount discount) throws IOException {
        FXMLLoader loader;
        loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/fxmls/home/discountCard.fxml"));
        loader.load();
        DiscountCardController discountCardController = loader.getController();
        discountCardController.setDiscountNameLabel(discount.getName());
        return discountCardController;
    }

    private Map<Integer, List<StoreItem>> createItemsListForEachStore() {
        Map<Integer, Double> itemsToOrderWithAmount;
        itemsToOrderWithAmount = generateItemsToOrderWithAmountWithoutDuplicates(this.currentOrder);
        Map<Integer, Store> cheapestStoreForEachItemInOrder = this.mainController.getSDMLogic().getCheapestStoresPerProductMap(itemsToOrderWithAmount);
        List<StoreItem> itemsToOrder = this.mainController.getSDMLogic().createListOfOrderedItemsByCheapestPrice(itemsToOrderWithAmount, cheapestStoreForEachItemInOrder);
        //itemsToOrder -> contains StoreItem for each of the items in order with it's lowest price set.
        //Integer -> StoreID, List<StoreItem> -> List of Items to order from that store.
        //itemsListForEachStore is our main focus now :)
        return this.mainController.getSDMLogic().generateItemsListForEachStore(itemsToOrder, cheapestStoreForEachItemInOrder);
    }

    private Map<Integer, Double> generateItemsToOrderWithAmountWithoutDuplicates(Order currentOrder) {
        Map<Integer, Double> outputItemsMapWithAmount = new HashMap<>();
        for (StoreItem sItem : currentOrder.getItemsInOrder()) {
            if(outputItemsMapWithAmount.containsKey(sItem.getId())) {
                outputItemsMapWithAmount.put(sItem.getId(), outputItemsMapWithAmount.get(sItem.getId()) + sItem.getTotalItemsSold());
            }
            else { outputItemsMapWithAmount.put(sItem.getId(), sItem.getTotalItemsSold()); }
        }

        return outputItemsMapWithAmount;
    }

    @FXML
    void onActionDynamicPurchaseButton() {
        try {
            if(!isDynamicPurchaseButtonToggled){
                this.shoppingCartTableView.getItems().clear();
                resetOrderProperties();
                resetSelectedStoreCards();
                this.currentOrder.clearOrderDetails();
                this.discountsAvailable.clear();
                this.discountsCardsHBox.getChildren().clear();
            }

            this.isDynamicPurchaseButtonToggled = true;
            if(wasOneOfPurchaseButtonsClicked.get()) {
                wasOneOfPurchaseButtonsClicked.set(false);
            }

            this.storeCardsVBox.setDisable(true);
            this.purchaseItemsAvailableTableView.getItems().clear();
            ObservableList<StoreItem> observableSystemItemsList = FXCollections.observableArrayList();
            observableSystemItemsList.addAll(this.mainController.getSDMLogic().getItems().values());
            purchaseItemsAvailableTableView.setItems(observableSystemItemsList);
            this.chooseItemToBuyComboBox.setItems(observableSystemItemsList);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onActionStaticPurchaseButton() {

        if(isDynamicPurchaseButtonToggled) {
            resetOrderProperties();
            this.discountsCardsHBox.getChildren().clear();
            this.shoppingCartTableView.getItems().clear();
            this.purchaseItemsAvailableTableView.getItems().clear();
            this.currentOrder.clearOrderDetails();
            this.discountsAvailable.clear();
        }

        this.isDynamicPurchaseButtonToggled = false;

        if(wasOneOfPurchaseButtonsClicked.get()) {
            wasOneOfPurchaseButtonsClicked.set(false);
        }

        this.storeCardsVBox.setDisable(false);
    }

    public void bindRelevantLabelsToOrderProperties() {
        this.totalCartPriceLabel.textProperty().bind(cartPriceProperty.asString());
        this.totalItemsLabel.textProperty().bind(totalItemsProperty.asString());
        this.totalItemTypesLabel.textProperty().bind(itemTypesProperty.asString());
    }

    private void resetOrderProperties(){
        itemTypesProperty.set(0);
        totalItemsProperty.set(0);
        cartPriceProperty.set(0);
    }

    @FXML
    void onActionAddToCartButton() {
        try {
            Store storeToBuyFrom;
            StringBuilder sb = new StringBuilder();
            String textFieldValue = this.itemAmountToBuyTextField.getText();
            StoreItem selectedItem = this.chooseItemToBuyComboBox.getSelectionModel().getSelectedItem();
            if(selectedItem != null) {
                if(isValidTextFieldValue(selectedItem.getPurchaseCategory(), textFieldValue, sb)) {
                    StoreItem newStoreItem = new StoreItem(selectedItem, Double.parseDouble(textFieldValue));
                    this.itemAmountToBuyTextField.setStyle("-fx-border-color: none;");
                    this.itemAmountToBuyTextField.setPromptText("Amount");
                    storeToBuyFrom = getStoreToBuyFrom(newStoreItem); //according to static/dynamic purchase.
                    invokeAllOnActionAddToCartButtonMethods(newStoreItem, storeToBuyFrom);
                }
                else { displayVisualInvalidInput(sb); }
                this.itemAmountToBuyTextField.setText("");
            }
            else { displayChooseItemFirstError(); }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void invokeAllOnActionAddToCartButtonMethods(StoreItem newStoreItem, Store storeToBuyFrom) {
        updateDiscountsTableViewIfNeeded(newStoreItem, storeToBuyFrom);
        newStoreItem.setPricePerUnit(storeToBuyFrom.getItemsBeingSold().get(newStoreItem.getId()).getPricePerUnit());
        updateCartTableView(newStoreItem);
        addItemToOrder(newStoreItem);
        updateProperties(newStoreItem, storeToBuyFrom);
        addStoreParticipatingInOrder(storeToBuyFrom);
    }

    private Store getStoreToBuyFrom(StoreItem newStoreItem) {
        Store storeToBuyFrom;

        if(this.isDynamicPurchaseButtonToggled){
            storeToBuyFrom = this.mainController.getSDMLogic().getStoreForDynamicPurchase(newStoreItem.getId());
        }
        else {
            storeToBuyFrom = this.mainController.getSDMLogic().getStores().get(this.currentStaticStoreId);
        }

        return storeToBuyFrom;
    }

    private void displayChooseItemFirstError() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Purchase Demand Error");
        alert.setHeaderText(null);
        alert.setContentText("Please choose an item first");
        alert.showAndWait();
    }

    private void displayEmptyCartError() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Purchase Demand Error");
        alert.setHeaderText(null);
        alert.setContentText("Please add at least one item to the cart");
        alert.showAndWait();
    }

    private void displayVisualInvalidInput(StringBuilder sb) {
        this.itemAmountToBuyTextField.setStyle("-fx-border-color: red;");
        this.itemAmountToBuyTextField.setPromptText(sb.toString());
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

    private void setStoreFinalOrderDetailsTableColumnsProperties() {

    }

    private void setDiscountOffersTableColumnsProperties() {
    }

    private void setShoppingCartTableColumnsProperties() {
        this.shoppingCartTableViewItemNameColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, String>("name"));
        this.shoppingCartTableViewAmountColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Double>("totalItemsSold"));
        this.shoppingCartTableViewTotalPriceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<Double>(cellData.getValue().getTotalPrice()));
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    private void setPurchaseItemsAvailableTableColumnsProperties() {
        this.purchaseItemTableViewItemIdColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
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
                public void handle(MouseEvent event) { staticStoreCardOnClick(event); }
            });
        }
        storeCardsVBox.setDisable(true);
    }

    private void staticStoreCardOnClick(MouseEvent event) {
        AnchorPane storeCardMainRoot = (AnchorPane) event.getSource();
        Pane pane = (Pane)storeCardMainRoot.getChildren().get(0);
        int storeId = Integer.parseInt(((Label)pane.getChildren().get(1)).textProperty().get());
        Store store = mainController.getSDMLogic().getStores().get(storeId);
        updatePurchaseItemAvailableTableView(store);
        updatePurchaseItemsAvailableComboBox(store);
        updateShoppingCartWhenCardClicked(storeId);
        resetSelectedStoreCards();
        setSelectedStoreCardBorder(storeCardMainRoot);
        updateCurrentStaticStoreAndResetOrderProperties(storeId);
        this.currentStaticStoreId = storeId;
    }

    private void updateCurrentStaticStoreAndResetOrderProperties(int storeId) {
        if(currentStaticStoreId != storeId) {
            resetOrderProperties();
            currentStaticStoreId = storeId;
        }
    }

    private void setSelectedStoreCardBorder(AnchorPane storeCardMainRoot) {
        storeCardMainRoot.setStyle("-fx-border-color: blue; -fx-background-color: #EEEEFB;");
    }

    private void resetSelectedStoreCards() {
        this.mainController.getStoreCardControllerMapForPurchase().values().forEach(storeCardController -> {
            storeCardController.getMainRoot().setStyle("-fx-border-color: none; -fx-background-color: #EEEEFB;");
        });
    }

    private void updateShoppingCartWhenCardClicked(int storeId) {
        if(this.currentStaticStoreId != storeId){
            this.shoppingCartTableView.getItems().clear();
            this.currentOrder.clearOrderDetails();
            this.discountsAvailable.clear();
        }
    }

    private void updateProperties(StoreItem newStoreItem, Store storeToBuyFrom) {
        this.cartPriceProperty.set(this.cartPriceProperty.get() + newStoreItem.getTotalPrice());
        this.totalItemsProperty.set(this.currentOrder.getTotalNumberOfItemsInOrder());
        this.itemTypesProperty.set(this.currentOrder.getNumberOfItemsTypesInOrder());
    }

    private void addStoreParticipatingInOrder(Store storeToBuyFrom) {
        if(!storesParticipatingInOrder.containsKey(storeToBuyFrom.getId())) {
            storesParticipatingInOrder.put(storeToBuyFrom.getId(), storeToBuyFrom);
        }
    }

    private void addItemToOrder(StoreItem newStoreItem) {
        this.currentOrder.addItem(newStoreItem);
    }

    private void updateCartTableView(StoreItem newStoreItem) {
        this.shoppingCartTableView.getItems().add(newStoreItem);
    }

    private void updateDiscountsTableViewIfNeeded(StoreItem newStoreItem, Store storeToBuyFrom) {

    }

    private boolean isValidTextFieldValue(String purchaseCategory, String textFieldValue, StringBuilder outMessage) {
        if(purchaseCategory.equals("Quantity")) {
            if(isIntegerNumber(textFieldValue)) {
                return true;
            }
            else {
                outMessage.append("Input is not an integer");
                return false;
            }
        }
        else {
            if(isNumber(textFieldValue)) {
                return true;
            }
            else {
                outMessage.append("Input is not a number");
                return false;
            }
        }
    }

    private boolean isNumber(String textFieldValue) {
        try {
            Double.parseDouble(textFieldValue);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    private void updatePurchaseItemsAvailableComboBox(Store store) {
        ObservableList<StoreItem> observableSystemItemsList = FXCollections.observableArrayList();
        observableSystemItemsList.addAll(store.getItemsBeingSold().values());
        this.chooseItemToBuyComboBox.setItems(observableSystemItemsList);
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

    private boolean isIntegerNumber(String numberToCheck) {
        try {
            Integer.parseInt(numberToCheck);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}
