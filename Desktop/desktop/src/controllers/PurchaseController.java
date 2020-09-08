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
import java.util.concurrent.atomic.AtomicBoolean;

public class PurchaseController {

    private AppController mainController;
    private int currentStaticStoreId = -1;
    private boolean isDynamicPurchaseButtonToggled;
    private final Order currentOrder = new Order();
    private final Map<Integer, Store> storesParticipatingInOrder = new HashMap<>();
    private Map<Integer, Double> itemsAmountBucketMap = new HashMap<>();
    private Map<Integer, List<StoreItem>> dynamicOrder;
    private HashMap<Integer, List<StoreItem>> tempStaticOrder = new HashMap<>();
    private final List<DiscountCardController> discountCardControllersList = new ArrayList<>();

    //Properties
    private SimpleDoubleProperty cartPriceProperty = new SimpleDoubleProperty();
    private SimpleDoubleProperty deliveryCostProperty = new SimpleDoubleProperty();
    private SimpleIntegerProperty itemTypesProperty = new SimpleIntegerProperty();
    private SimpleIntegerProperty totalItemsProperty = new SimpleIntegerProperty();
    private SimpleBooleanProperty wasOneOfPurchaseButtonsClicked;
    private SimpleBooleanProperty wasBuyCartButtonClicked = new SimpleBooleanProperty();
    private SimpleBooleanProperty isShowFinalSummaryStage = new SimpleBooleanProperty();

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
    private Label discountOffersLabel;

    @FXML
    private ComboBox<Customer> customerComboBox;

    @FXML
    private HBox discountOffersHBox;

    @FXML
    private Label discountAvailableLabel;

    @FXML
    private Button displayOrderSummaryButton;

    @FXML
    private ScrollPane discountAvailableScrollPane;

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
    private TableView<Discount.ThenGet.Offer> discountOffersTableView;

    @FXML
    private TableColumn<Discount.ThenGet.Offer, String> discountOffersTableViewItemNameColumn;

    @FXML
    private TableColumn<Discount.ThenGet.Offer, Double> discountOffersTableViewQuantityColumn;

    @FXML
    private TableColumn<Discount.ThenGet.Offer, Double> discountOffersTableViewForAdditionalColumn;

    @FXML
    private Label discountOperatorLabel;

    @FXML
    private ComboBox<Discount.ThenGet.Offer> chooseOfferComboBox;

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
        bindRelevantObjectToWasBuyCartButtonClicked();
        setBuyCartEventHandler();

    }

    private void bindRelevantObjectToWasBuyCartButtonClicked() {
        //disabled property
        isShowFinalSummaryStage.set(false);
        this.buyCartButton.disableProperty().bind(this.wasBuyCartButtonClicked);
        this.addItemToCartButton.disableProperty().bind(this.wasBuyCartButtonClicked);
        this.applyDiscountButton.disableProperty().bind(this.wasBuyCartButtonClicked.not());
        //Discount section
        this.discountOffersHBox.disableProperty().bind(this.wasBuyCartButtonClicked.not());
        this.discountAvailableLabel.disableProperty().bind(this.wasBuyCartButtonClicked.not());
        this.discountOffersLabel.disableProperty().bind(this.wasBuyCartButtonClicked.not());
        this.discountOffersTableView.disableProperty().bind(this.wasBuyCartButtonClicked.not());
        this.displayOrderSummaryButton.disableProperty().bind(this.wasBuyCartButtonClicked.not());
        this.discountAvailableScrollPane.disableProperty().bind(this.wasBuyCartButtonClicked.not());
        //visible properties
        this.discountOffersHBox.visibleProperty().bind(this.wasBuyCartButtonClicked);
        this.discountAvailableLabel.visibleProperty().bind(this.wasBuyCartButtonClicked);
        this.discountOffersLabel.visibleProperty().bind(this.wasBuyCartButtonClicked);
        this.discountOffersTableView.visibleProperty().bind(this.wasBuyCartButtonClicked);
        this.displayOrderSummaryButton.visibleProperty().bind(this.wasBuyCartButtonClicked);
        this.discountAvailableScrollPane.visibleProperty().bind(this.wasBuyCartButtonClicked);
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
        Map<Integer, List<StoreItem>> purchaseItemsMap; // Integer -> storeId, List<StoreItem> -> StoreItems to buy from the store.
        if(!shoppingCartTableView.getItems().isEmpty()){
            if(this.isDynamicPurchaseButtonToggled) { this.dynamicOrder = initializeDynamicOrderAndAddDiscountCards(); }
            else {
                tempStaticOrder.put(this.currentStaticStoreId, currentOrder.getItemsInOrder());
                addStaticOrderDiscountsCards();
            }

            if(!discountCardControllersList.isEmpty()) {
                this.wasBuyCartButtonClicked.set(true);
                //There are available discounts to apply
                //initializeItemAmountBucketMap(dynamic/static); already happened
            }
            else { showAndInitializeFinalBuySummary(); }
        }
        else { displayEmptyCartError(); }
    }

    private void showAndInitializeFinalBuySummary() {
        makeInvisibleAndDisableDiscountsSection();
    }

    private void makeInvisibleAndDisableDiscountsSection() {
        this.wasBuyCartButtonClicked.set(false);
    }

    private void addStaticOrderDiscountsCards() {
        //In static order we have the final order and the store we buy from.
        Map<Integer, List<StoreItem>> staticPurchaseItemsMap = new HashMap<>();
        staticPurchaseItemsMap.put(this.currentStaticStoreId, this.currentOrder.getItemsInOrder());
        initializeItemAmountBucketMap(staticPurchaseItemsMap);
        addRelevantDiscountCardsToHBox(staticPurchaseItemsMap);
    }

    private Map<Integer, List<StoreItem>> initializeDynamicOrderAndAddDiscountCards() {
        Map<Integer, List<StoreItem>> itemListForEachStoreMap = createItemsListForEachStore();
        initializeItemAmountBucketMap(itemListForEachStoreMap);
        addRelevantDiscountCardsToHBox(itemListForEachStoreMap);
        return itemListForEachStoreMap;
    }

    private void initializeItemAmountBucketMap(Map<Integer, List<StoreItem>> itemListForEachStoreMap) {
        this.itemsAmountBucketMap.clear();
        itemListForEachStoreMap.forEach((storeId, listOfItems) -> {
            for (StoreItem sItem : listOfItems) {
                if(this.itemsAmountBucketMap.containsKey(sItem.getId())) {
                    this.itemsAmountBucketMap.put(sItem.getId(),this.itemsAmountBucketMap.get(sItem.getId()) + sItem.getTotalItemsSold());
                }
                else {
                    this.itemsAmountBucketMap.put(sItem.getId(), sItem.getTotalItemsSold());
                }
            }
        });
    }

    private void addRelevantDiscountCardsToHBox(Map<Integer, List<StoreItem>> itemListForEachStoreMap) {
        this.discountsCardsHBox.getChildren().clear();
        this.discountCardControllersList.clear();

        itemListForEachStoreMap.forEach((storeId, itemListForTheStore) -> {
            Store currentStore = this.mainController.getSDMLogic().getStores().get(storeId);
            //There are no duplicates in the input map;
            for (StoreItem sItem : itemListForTheStore ) {
                if(currentStore.getStoreDiscounts() != null) {
                    if(currentStore.getStoreDiscounts().containsKey(sItem.getId())) {
                        List<Discount> discountsForThatItem = currentStore.getStoreDiscounts().get(sItem.getId());
                        List<Discount> discountToAdd = new ArrayList<>();
                        try {
                            for (Discount discount : discountsForThatItem) {
                                if(this.itemsAmountBucketMap.containsKey(sItem.getId())){
                                    if (this.itemsAmountBucketMap.get(sItem.getId()) >=  discount.getBuyThis().getQuantity()) {
                                        discountToAdd.add(discount);
                                    }
                                }
                            }

                            createAndAddDiscountCards(discountToAdd, currentStore.getId());
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
        for (DiscountCardController discountCard : this.discountCardControllersList) {
            this.discountsCardsHBox.getChildren().add(discountCard.getMainRoot());
            discountCard.getMainRoot().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) { discountCardClickMethod(event); }
            });
        }
    }

    private void discountCardClickMethod(MouseEvent event) {
        resetDiscountRelevantComponents();
        AnchorPane discountCardMainRoot = (AnchorPane) event.getSource();
        resetSelectedDiscountCards();
        setSelectedDiscountCardBorder(discountCardMainRoot);
        String discountName = ((Label)discountCardMainRoot.getChildren().get(1)).getText();
        String discountStoreIdString = ((Label)discountCardMainRoot.getChildren().get(2)).getText();
        String itemToBuyIdString = ((Label)((Pane)discountCardMainRoot.getChildren().get(0)).getChildren().get(0)).getText();
        int discountStoreId = Integer.parseInt(discountStoreIdString);
        int itemToBuyId = Integer.parseInt(itemToBuyIdString);
        loadDiscountDetailsInRelevantComponents(Objects.requireNonNull(getClickedDiscount(discountName, discountStoreId, itemToBuyId)));
    }

    private void resetDiscountRelevantComponents() {
        this.chooseOfferComboBox.getItems().clear();
        this.discountOffersTableView.getItems().clear();
        this.discountOperatorLabel.setText("");
    }

    private Discount getClickedDiscount(String discountName, int discountStoreId, int itemToBuyId) {
        List<Discount> discountsRelevantToTheItemToBuyId =
                this.mainController.getSDMLogic().getStores().get(discountStoreId).getStoreDiscounts().get(itemToBuyId);
        for (Discount discount : discountsRelevantToTheItemToBuyId) {
            if(discount.getName().equals(discountName)) { return discount; }
        }

        return null;
    }

    private void loadDiscountDetailsInRelevantComponents(Discount clickedDiscount) {
        ObservableList<Discount.ThenGet.Offer> observableDiscountOffersList = FXCollections.observableArrayList();
        observableDiscountOffersList.addAll(clickedDiscount.getGetThat().getOfferList());
        discountOffersTableView.setItems(observableDiscountOffersList);
        String thenYouGetOperator = clickedDiscount.getGetThat().getOperator();
        this.discountOperatorLabel.setText(thenYouGetOperator);
        this.chooseOfferComboBox.setDisable(thenYouGetOperator.equals("ALL-OR-NOTHING") || thenYouGetOperator.equals("IRRELEVANT"));
        this.chooseOfferComboBox.setItems(observableDiscountOffersList);
    }

    private void setSelectedDiscountCardBorder(AnchorPane discountCardMainRoot) {
        discountCardMainRoot.setStyle("-fx-border-color: blue; -fx-background-color: #EEEEFB;");
    }

    private void resetSelectedDiscountCards() {
        this.discountCardControllersList.forEach(discountCardController -> {
            discountCardController.getMainRoot().setStyle("-fx-border-color: none; -fx-background-color: #EEEEFB;");
        });
    }

    private void createAndAddDiscountCards(List<Discount> discountToAdd, int discountStoreId) throws IOException {
        try {
            for (Discount discount : discountToAdd) {
                if(!isDiscountControllerAlreadyExistInList(discount)) {
                    DiscountCardController discountCardController = createDiscountController(discount);
                    discountCardController.setDiscountCardItemToBuyId(discount.getBuyThis().getItemId());
                    discountCardController.setDiscountCardItemToBuyName(String.format(" | %s",discount.getItemToBuyName()));
                    discountCardController.setStoreId(discountStoreId);
                    this.discountCardControllersList.add(discountCardController);
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isDiscountControllerAlreadyExistInList(Discount discount) {
        AtomicBoolean isDiscountAlreadyExistInListFlag = new AtomicBoolean(false);
        for (DiscountCardController discountCardController : this.discountCardControllersList) {
            if(discount.getStoreIdOfDiscount() == discountCardController.getStoreIdOfDiscount()
                    && discount.getName().equals(discountCardController.getDiscountName())) {
                isDiscountAlreadyExistInListFlag.set(true);
                break;
            }
        }

        return isDiscountAlreadyExistInListFlag.get();
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
                resetOrderProperties();
                resetSelectedStoreCards();
                onChangedBuyResetProperties();
            }

            this.chooseOfferComboBox.setDisable(false);
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
            this.purchaseItemsAvailableTableView.getItems().clear();
            onChangedBuyResetProperties();
        }

        this.chooseOfferComboBox.setDisable(false);
        this.isDynamicPurchaseButtonToggled = false;

        if(wasOneOfPurchaseButtonsClicked.get()) {
            wasOneOfPurchaseButtonsClicked.set(false);
        }

        this.storeCardsVBox.setDisable(false);
    }

    @FXML
    void onActionApplyDiscountButton() {
        String offerOperator = this.discountOperatorLabel.getText();
        if( offerOperator.equals("ONE-OF")){
            Discount.ThenGet.Offer selectedOffer = this.chooseOfferComboBox.getSelectionModel().getSelectedItem();
            //Offer can be applied due to our logic flow. (All discount cards appear iff they can be applied)
            if(selectedOffer != null){
                createNewItemFromAppliedOfferAndUpdateTableViewAndOrderAndAddToRelevantOrder(selectedOffer);
                updatePropertiesFromOffer(selectedOffer);
            }
            else {
                displayChooseComboBoxItemFirstError();
            }
        }
        else {
            for (Discount.ThenGet.Offer offer : this.discountOffersTableView.getItems()) {
                //Offer can be applied due to our logic flow. (All discount cards appear iff they can be applied)
                createNewItemFromAppliedOfferAndUpdateTableViewAndOrderAndAddToRelevantOrder(offer);
                updatePropertiesFromOffer(offer);
            }
        }

        this.discountOffersTableView.getItems().clear();
        if(discountCardControllersList.isEmpty()){
            showAndInitializeFinalBuySummary();
        }
    }

    private void updatePropertiesFromOffer(Discount.ThenGet.Offer selectedOffer) {
        if(!isDynamicPurchaseButtonToggled){
            //static
            this.itemTypesProperty.set(this.currentOrder.getNumberOfItemsTypesInOrder());
            this.totalItemsProperty.set(this.currentOrder.getTotalNumberOfItemsInOrder());
            this.cartPriceProperty.set(currentOrder.getCostOfItemsInOrder());
        }
        else {
            int amountToAdd;
            this.itemTypesProperty.set(calculateAmountOfItemsTypesInDynamicOrder()); //dynamicOrder is a member of the controller
            if(this.mainController.getSDMLogic().getItems().get(selectedOffer.getOfferItemId()).getPurchaseCategory().equals("Weight")) { amountToAdd = 1; }
            else { amountToAdd = (int)selectedOffer.getQuantity(); }
            this.totalItemsProperty.set( this.totalItemsProperty.get() + amountToAdd);
            this.cartPriceProperty.set(this.cartPriceProperty.get() + selectedOffer.getForAdditional());
        }
    }

    private int calculateAmountOfItemsTypesInDynamicOrder() {
        HashSet<Integer> itemTypesHashSet = new HashSet<>();
        dynamicOrder.forEach((storeId,itemsToBuyList) -> {
            itemsToBuyList.forEach(item -> itemTypesHashSet.add(item.getId()));
        });

        return itemTypesHashSet.size();
    }

    private void createNewItemFromAppliedOfferAndUpdateTableViewAndOrderAndAddToRelevantOrder(Discount.ThenGet.Offer offer) {
        if(offer != null){
            Discount.IfBuy ifBuyItem = offer.getBuyThisItem();
            this.itemsAmountBucketMap.put(offer.getBuyThisItem().getItemId(),
                    this.itemsAmountBucketMap.get(ifBuyItem.getItemId()) - ifBuyItem.getQuantity());
            StoreItem newStoreItemFromOffer = new StoreItem(offer.getOfferItemId(), offer.getQuantity(),
                    offer.getForAdditional() / offer.getQuantity(), offer.getItemName(),
                    this.mainController.getSDMLogic().getItems().get(offer.getOfferItemId()).getPurchaseCategory(), true);
            addAppliedOfferToOrderAndUpdateCart(offer, newStoreItemFromOffer);
        }
    }

    private void addAppliedOfferToOrderAndUpdateCart(Discount.ThenGet.Offer selectedOffer, StoreItem newStoreItemFromOffer) {
        if(this.isDynamicPurchaseButtonToggled){
            //push to dynamic
            if(dynamicOrder.containsKey(selectedOffer.getStoreIdOfOffer())){
                dynamicOrder.get(selectedOffer.getStoreIdOfOffer()).add(newStoreItemFromOffer);
            }
            else {
                List<StoreItem> storeItemList = new ArrayList<>();
                storeItemList.add(newStoreItemFromOffer);
                dynamicOrder.put(selectedOffer.getStoreIdOfOffer(), storeItemList);
            }
            this.addRelevantDiscountCardsToHBox(this.dynamicOrder);
        }
        else {
            //push to static
            currentOrder.addItem(newStoreItemFromOffer);
            this.addRelevantDiscountCardsToHBox(tempStaticOrder);
        }

        addItemToCartTableView(newStoreItemFromOffer);
    }

    @FXML
    void onActionDisplayOrderSummaryButton(ActionEvent event) {
        showAndInitializeFinalBuySummary();
    }

    private void onChangedBuyResetProperties() {
        resetDiscountRelevantComponents();
        resetOrderProperties();//
        this.discountsCardsHBox.getChildren().clear();//
        this.shoppingCartTableView.getItems().clear();//
        this.currentOrder.clearOrderDetails();//
        this.wasBuyCartButtonClicked.set(false);//
        this.chooseItemToBuyComboBox.getItems().clear();
        this.discountCardControllersList.clear();
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
                    performAllOnActionAddToCartButtonMethods(newStoreItem, storeToBuyFrom);
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

    private void performAllOnActionAddToCartButtonMethods(StoreItem newStoreItem, Store storeToBuyFrom) {
        newStoreItem.setPricePerUnit(storeToBuyFrom.getItemsBeingSold().get(newStoreItem.getId()).getPricePerUnit());
        addItemToCartTableView(newStoreItem);
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

    private void displayChooseComboBoxItemFirstError() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("ComboBox Item Select Error");
        alert.setHeaderText(null);
        alert.setContentText("Please choose an offer first");
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
        //this.chooseOfferComboBox.disableProperty().bind(wasOneOfPurchaseButtonsClicked);
        this.chooseOfferComboBox.setDisable(true);
        this.applyDiscountButton.disableProperty().bind(wasOneOfPurchaseButtonsClicked);
        this.shoppingCartTableView.disableProperty().bind(wasOneOfPurchaseButtonsClicked);
        this.buyCartButton.disableProperty().bind(wasOneOfPurchaseButtonsClicked);
    }

    private void setStoreFinalOrderDetailsTableColumnsProperties() {

    }

    private void setDiscountOffersTableColumnsProperties() {
        this.discountOffersTableViewQuantityColumn.setCellValueFactory(new PropertyValueFactory<Discount.ThenGet.Offer, Double>("quantity"));
        this.discountOffersTableViewForAdditionalColumn.setCellValueFactory(new PropertyValueFactory<Discount.ThenGet.Offer, Double>("forAdditional"));
        this.discountOffersTableViewItemNameColumn.setCellValueFactory(cellData -> {
            String itemName = this.mainController.getSDMLogic().getItems().get(cellData.getValue().getOfferItemId()).getName();
            return new SimpleObjectProperty<>(itemName);
        });
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
        clearDiscountCardsFromHBoxIfNeeded(storeId);
        updateWasCartBuyButtonClickedIfNeeded(storeId);
        this.currentStaticStoreId = storeId;

    }

    private void updateWasCartBuyButtonClickedIfNeeded(int storeId) {
        if(currentStaticStoreId != storeId){
            this.wasBuyCartButtonClicked.set(false);
        }
    }

    private void clearDiscountCardsFromHBoxIfNeeded(int storeId) {
        if(this.currentStaticStoreId != storeId){
            this.discountCardControllersList.clear();
            this.discountsCardsHBox.getChildren().clear();
            resetDiscountRelevantComponents();
        }
    }

    private void updateCurrentStaticStoreAndResetOrderProperties(int storeId) {
        if(currentStaticStoreId != storeId) {
            resetOrderProperties();
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

    private void addItemToCartTableView(StoreItem newStoreItem) {
        this.shoppingCartTableView.getItems().add(newStoreItem);
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
