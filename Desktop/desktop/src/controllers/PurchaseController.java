package controllers;

import SDMImprovedFacade.*;
import javafx.beans.binding.Bindings;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PurchaseController {
    private AppController mainController;
    private int currentStaticStoreId = -1;
    private boolean isDynamicPurchaseButtonToggled;
    private Order currentOrder = new Order();
    private final Map<Integer, Store> storesParticipatingInOrder = new HashMap<>();
    private Map<Integer, Double> itemsAmountBucketMap = new HashMap<>();
    private Map<Integer, List<StoreItem>> dynamicOrder;
    private HashMap<Integer, List<StoreItem>> tempStaticOrder = new HashMap<>();
    private final List<DiscountCardController> discountCardControllersList = new ArrayList<>();

    //Properties
    private final SimpleDoubleProperty cartPriceProperty = new SimpleDoubleProperty();
    private final SimpleDoubleProperty totalDeliveryCostProperty = new SimpleDoubleProperty();
    private final SimpleIntegerProperty itemTypesProperty = new SimpleIntegerProperty();
    private final SimpleIntegerProperty totalItemsProperty = new SimpleIntegerProperty();
    private SimpleBooleanProperty wasOneOfPurchaseButtonsClicked;
    private final SimpleBooleanProperty wasBuyCartButtonClicked = new SimpleBooleanProperty();
    private final SimpleBooleanProperty isShowFinalSummaryStage = new SimpleBooleanProperty();
    private final SimpleBooleanProperty wasCustomerSelected = new SimpleBooleanProperty(false);

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
    private Label storeFinalInformationHBoxStoreDeliveryCostLabel;

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
    private TableView<StoreItem> storeFinalOrderDetailsTableView;

    @FXML
    private TableColumn<StoreItem, Integer> storeFinalOrderDetailsTableViewItemIdColumn;

    @FXML
    private TableColumn<StoreItem, String> storeFinalOrderDetailsTableViewNameColumn;

    @FXML
    private TableColumn<StoreItem, String> storeFinalOrderDetailsTableViewCategoryColumn;

    @FXML
    private TableColumn<StoreItem, Double> storeFinalOrderDetailsTableViewQuantityColumn;

    @FXML
    private TableColumn<StoreItem, Double> storeFinalOrderDetailsTableViewPricePerUnitColumn;

    @FXML
    private TableColumn<StoreItem, Double> storeFinalOrderDetailsTableViewTotalPriceColumn;

    @FXML
    private TableColumn<StoreItem, Boolean> storeFinalOrderDetailsTableViewIsPartOfDiscountColumn;

    @FXML
    private ComboBox<Store> selectStoreToViewItsOrderDetailsComboBox;

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
    private Label storeFinalInformationHBoxTotalOrderCostLabel;

    @FXML
    private HBox finalSummaryTotalOrderAndDeliveryCostHBox;

    @FXML
    private Button resetOrderButton;

    @FXML
    private void initialize(){
        this.wasOneOfPurchaseButtonsClicked = new SimpleBooleanProperty(true);
        setPurchaseItemsAvailableTableColumnsProperties();
        setShoppingCartTableColumnsProperties();
        setDiscountOffersTableColumnsProperties();
        setStoreFinalOrderDetailsTableColumnsProperties();
        bindRelevantObjectsToWasOneOfPurchaseButtonsClicked();
        bindRelevantObjectToWasBuyCartButtonClicked();
        bindRelevantObjecetsToIsShowFinalSummaryStage();
        bindRelevantLabelsOfFinalOrderSummaryToDeliveryAndOrderCostProperties();
        setBuyCartEventHandler();
        
    }

    private void bindRelevantLabelsOfFinalOrderSummaryToDeliveryAndOrderCostProperties() {
        this.storeFinalInformationHBoxTotalDeliveryCostLabel.textProperty().bind(this.totalDeliveryCostProperty.asString("%.2f"));
        this.storeFinalInformationHBoxTotalOrderCostLabel.textProperty().bind(
                Bindings.add(this.totalDeliveryCostProperty, this.cartPriceProperty).asString("%.2f"));
    }

    private void bindRelevantObjecetsToIsShowFinalSummaryStage() {

        //visible property
        this.finalSummaryTotalOrderAndDeliveryCostHBox.visibleProperty().bind(Bindings.and(wasCustomerSelected,isShowFinalSummaryStage));
        this.acceptRejectOrderHBox.visibleProperty().bind(Bindings.and(wasCustomerSelected,isShowFinalSummaryStage));
        this.storeFinalOrderDetailsTableView.visibleProperty().bind(Bindings.and(wasCustomerSelected,isShowFinalSummaryStage));
        this.storeFinalInformationHBox.visibleProperty().bind(Bindings.and(wasCustomerSelected,isShowFinalSummaryStage));
        this.selectStoreToViewItsOrderDetailsComboBox.visibleProperty().bind(Bindings.and(wasCustomerSelected,isShowFinalSummaryStage));
        //disable property
        this.selectStoreToViewItsOrderDetailsComboBox.disableProperty().bind(Bindings.and(wasCustomerSelected.not(),isShowFinalSummaryStage.not()));
        this.storeFinalInformationHBox.disableProperty().bind(Bindings.and(wasCustomerSelected.not(),isShowFinalSummaryStage.not()));
        this.acceptRejectOrderHBox.disableProperty().bind(Bindings.and(wasCustomerSelected.not(),isShowFinalSummaryStage.not()));
        this.storeFinalOrderDetailsTableView.disableProperty().bind(Bindings.and(wasCustomerSelected.not(),isShowFinalSummaryStage.not()));
    }

    private void bindRelevantObjectToWasBuyCartButtonClicked() {
        //disabled property
        isShowFinalSummaryStage.set(false);

        this.buyCartButton.disableProperty().bind(Bindings.or(this.wasBuyCartButtonClicked,isShowFinalSummaryStage));
        this.addItemToCartButton.disableProperty().bind(Bindings.or(this.wasBuyCartButtonClicked,isShowFinalSummaryStage));

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
            else {
                this.isShowFinalSummaryStage.set(true);
                if(wasCustomerSelected.get()) { showAndInitializeFinalBuySummary(); }
                else { displayCustomerNotChosenError(); }
            }
        }
        else { displayEmptyCartError(); }
    }

    private void showAndInitializeFinalBuySummary() {
        if(customerComboBox.getSelectionModel().getSelectedItem() != null)
        {
            loadDataIntoRelevantTotalSummaryComponents();
            makeInvisibleAndDisableDiscountsSection();
        }
        else { displayCustomerNotChosenError(); }
    }

    private void loadDataIntoRelevantTotalSummaryComponents() {
        resetAllComponents();
        loadDataIntoFinalSummaryComboBox();
        updateTotalDeliveryCostProperty();
    }

    private void updateTotalDeliveryCostProperty() {
        double totalDeliveryCost = 0;
        if(!isDynamicPurchaseButtonToggled){
            //static
            Store store = this.mainController.getSDMLogic().getStores().get(this.currentStaticStoreId);
            totalDeliveryCost = store.getDeliveryPpk() * store.calculateDistance(customerComboBox.getSelectionModel().getSelectedItem().getLocation());
        }
        else {
            //dynamic
            for (Integer storeId : this.dynamicOrder.keySet()) {
                Store store = this.mainController.getSDMLogic().getStores().get(storeId);
                totalDeliveryCost += store.getDeliveryPpk() * store.calculateDistance(customerComboBox.getSelectionModel().getSelectedItem().getLocation());
            }
        }

        this.totalDeliveryCostProperty.set(totalDeliveryCost);
    }

    private void loadDataIntoFinalSummaryComboBox() {
        ObservableList<Store> storesObservableList = FXCollections.observableArrayList();
        Collection<Store> currentStores = new ArrayList<>();
        storesObservableList.addAll();
        if(!isDynamicPurchaseButtonToggled) {
            //static
            currentStores.add(this.mainController.getSDMLogic().getStores().get(currentStaticStoreId));
        }
        else {
            //dynamic
            this.dynamicOrder.forEach((storeId, items) -> currentStores.add(this.mainController.getSDMLogic().getStores().get(storeId)));
        }
        storesObservableList.addAll(currentStores);
        this.selectStoreToViewItsOrderDetailsComboBox.setItems(storesObservableList);
    }

    private void resetAllComponents() {
        resetFinalSummaryLabels();
        this.selectStoreToViewItsOrderDetailsComboBox.getItems().clear();
        this.storeFinalOrderDetailsTableView.getItems().clear();

    }

    private void resetFinalSummaryLabels() {
        this.storeFinalInformationHBoxDistanceFromCustomerLabel.setText("0");
        this.storeFinalInformationHBoxPpkLabel.setText("0");
        this.storeFinalInformationHBoxStoreDeliveryCostLabel.setText("0");
    }

    private void makeInvisibleAndDisableDiscountsSection() {
        this.wasBuyCartButtonClicked.set(false);
        isShowFinalSummaryStage.set(true);
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
        this.discountCardControllersList.forEach(discountCardController -> discountCardController.getMainRoot().setStyle("-fx-border-color: none; -fx-background-color: #EEEEFB;"));
    }

    private void createAndAddDiscountCards(List<Discount> discountToAdd, int discountStoreId) throws IOException {
        try {
            for (Discount discount : discountToAdd) {
                if(!isDiscountControllerAlreadyExistInList(discount)) {
                    DiscountCardController discountCardController = createDiscountController(discount);
                    discountCardController.setDiscountCardItemToBuyId(discount.getBuyThis().getItemId());
                    discountCardController.setDiscountCardItemToBuyName(String.format(" | %s",discount.getItemToBuyName()));
                    discountCardController.setStoreId(discountStoreId);
                    discountCardController.setStoreName(this.mainController.getSDMLogic().getStores().get(discountStoreId).getName());
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
            this.isShowFinalSummaryStage.set(true);
            if(wasCustomerSelected.get()){ showAndInitializeFinalBuySummary(); }
            else { displayCustomerNotChosenError(); }
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
        dynamicOrder.forEach((storeId,itemsToBuyList) -> itemsToBuyList.forEach(item -> itemTypesHashSet.add(item.getId())));

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
        currentOrder.addItem(newStoreItemFromOffer);

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
            this.addRelevantDiscountCardsToHBox(tempStaticOrder);
        }

        addItemToCartTableView(newStoreItemFromOffer);
    }

    @FXML
    void onActionDisplayOrderSummaryButton() {
        this.isShowFinalSummaryStage.set(true);
        showAndInitializeFinalBuySummary();
    }

    private void onChangedBuyResetProperties() {
        resetDiscountRelevantComponents();
        resetOrderProperties();
        this.discountsCardsHBox.getChildren().clear();
        this.shoppingCartTableView.getItems().clear();
        this.currentOrder.clearOrderDetails();
        this.wasBuyCartButtonClicked.set(false);
        this.chooseItemToBuyComboBox.getItems().clear();
        this.discountCardControllersList.clear();
        isShowFinalSummaryStage.set(false);
        this.customerComboBox.disableProperty().set(false);
        this.deliveryDatePicker.setValue(null);
    }

    public void bindRelevantLabelsToOrderProperties() {
        this.totalCartPriceLabel.textProperty().bind(cartPriceProperty.asString("%.2f"));
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
        updateProperties(newStoreItem);
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
        this.storeFinalOrderDetailsTableViewItemIdColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
        this.storeFinalOrderDetailsTableViewNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.storeFinalOrderDetailsTableViewCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseCategory"));
        this.storeFinalOrderDetailsTableViewQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("totalItemsSold"));
        this.storeFinalOrderDetailsTableViewPricePerUnitColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        this.storeFinalOrderDetailsTableViewTotalPriceColumn.setCellValueFactory(cellData ->  new SimpleObjectProperty<>(cellData.getValue().getTotalPrice()));
        this.storeFinalOrderDetailsTableViewIsPartOfDiscountColumn.setCellValueFactory(new PropertyValueFactory<>("wasPartOfDiscount"));
    }

    private void setDiscountOffersTableColumnsProperties() {
        this.discountOffersTableViewQuantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        this.discountOffersTableViewForAdditionalColumn.setCellValueFactory(new PropertyValueFactory<>("forAdditional"));
        this.discountOffersTableViewItemNameColumn.setCellValueFactory(cellData -> {
            String itemName = this.mainController.getSDMLogic().getItems().get(cellData.getValue().getOfferItemId()).getName();
            return new SimpleObjectProperty<>(itemName);
        });
    }

    private void setShoppingCartTableColumnsProperties() {
        this.shoppingCartTableViewItemNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.shoppingCartTableViewAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalItemsSold"));
        this.shoppingCartTableViewTotalPriceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTotalPrice()));
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    private void setPurchaseItemsAvailableTableColumnsProperties() {
        this.purchaseItemTableViewItemIdColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
        this.purchaseItemTableViewItemNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.purchaseItemTableViewPricePerUnitColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        this.purchaseItemTableViewCategoryColumn.setCellValueFactory(new PropertyValueFactory<>("purchaseCategory"));
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
        isShowFinalSummaryStage.set(false);
        this.customerComboBox.disableProperty().set(false);
        this.deliveryDatePicker.setValue(null);
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
        this.mainController.getStoreCardControllerMapForPurchase().values().forEach(storeCardController ->
                storeCardController.getMainRoot().setStyle("-fx-border-color: none; -fx-background-color: #EEEEFB;"));
    }

    private void updateShoppingCartWhenCardClicked(int storeId) {
        if(this.currentStaticStoreId != storeId){
            this.shoppingCartTableView.getItems().clear();
            this.currentOrder.clearOrderDetails();
        }
    }

    private void updateProperties(StoreItem newStoreItem) {
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
            Map<Integer, StoreItem> itemsToAdd = new HashMap<>(store.getItemsBeingSold());

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

    @FXML
    void onActionFinalSummaryComboBoxStoreSelected() {
        Store selectedStore = this.selectStoreToViewItsOrderDetailsComboBox.getSelectionModel().getSelectedItem();
        ObservableList<StoreItem> observableStoreItemsList = FXCollections.observableArrayList();
        if(selectedStore != null){
            updateFinalSummaryLabels(selectedStore);

            if(!isDynamicPurchaseButtonToggled) {
                this.storeFinalOrderDetailsTableView.getItems().clear();
                observableStoreItemsList.addAll(currentOrder.getItemsInOrder());
            }
            else{ observableStoreItemsList.addAll(this.dynamicOrder.get(selectedStore.getId())); }

            this.storeFinalOrderDetailsTableView.setItems(observableStoreItemsList);
        }
    }

    private void displayCustomerNotChosenError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Customer Selection Error");
        alert.setHeaderText(null);
        alert.setContentText("Please select a customer in order to advance to the final summary section");
        alert.showAndWait();
    }

    private void updateFinalSummaryLabels(Store selectedStore) {

        double distanceFromCustomer = selectedStore.calculateDistance(this.customerComboBox.getSelectionModel().getSelectedItem().getLocation());
        double deliveryCost = selectedStore.getDeliveryPpk() * distanceFromCustomer;
        this.storeFinalInformationHBoxDistanceFromCustomerLabel.setText(String.format("%.2f", distanceFromCustomer));
        this.storeFinalInformationHBoxStoreDeliveryCostLabel.setText(String.format("%.2f", deliveryCost));
        this.storeFinalInformationHBoxPpkLabel.setText(Integer.toString(selectedStore.getDeliveryPpk()));
    }

    @FXML
    void onActionResetOrderButton(ActionEvent event) {
        resetAllOrder();
    }

    private void resetAllOrder() {
        this.discountOffersTableView.getItems().clear();
        resetSelectedStoreCards();
        resetDiscountRelevantComponents();
        resetOrderProperties();

        this.currentStaticStoreId = -1;
        this.currentOrder.clearOrderDetails();
        if(this.dynamicOrder != null && !this.dynamicOrder.isEmpty()) { this.dynamicOrder.clear(); this.dynamicOrder = null; }
        this.discountsCardsHBox.getChildren().clear();
        this.shoppingCartTableView.getItems().clear();
        this.chooseItemToBuyComboBox.getItems().clear();
        this.itemsAmountBucketMap.clear();
        this.chooseOfferComboBox.getItems().clear();
        this.deliveryDatePicker.setValue(null);

        this.discountCardControllersList.clear();
        this.selectStoreToViewItsOrderDetailsComboBox.getItems().clear();
        this.storeFinalOrderDetailsTableView.getItems().clear();
        this.purchaseItemsAvailableTableView.getItems().clear();

        this.wasBuyCartButtonClicked.set(false);
        this.isShowFinalSummaryStage.set(false);
        this.customerComboBox.getItems().clear();
        this.wasCustomerSelected.set(false);
        this.customerComboBox.disableProperty().set(false);
        insertCustomersToComboBox();
        this.wasOneOfPurchaseButtonsClicked.set(false);
    }

    @FXML
    void onActionCustomerSelectComboBox() {
        if(this.customerComboBox.getSelectionModel().getSelectedItem() != null) {
            wasCustomerSelected.set(true);
            this.customerComboBox.disableProperty().set(true);
        }

        if(this.isShowFinalSummaryStage.get() && wasCustomerSelected.get()) {
            showAndInitializeFinalBuySummary();
        }
    }

    @FXML
    void onActionRejectOrderButton() {
        resetAllOrder();
    }


    @FXML
    void onActionAcceptOrderButton() {
        if(deliveryDatePicker.getValue() != null) {
            int lastOrderId = this.mainController.getSDMLogic().getLastOrderID();
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            String dateOfOrder = deliveryDatePicker.getValue().format(dateTimeFormatter);
            Customer customer = customerComboBox.getSelectionModel().getSelectedItem();

            if(!isDynamicPurchaseButtonToggled) {
                Store storeToOrderFrom = this.mainController.getSDMLogic().getStores().get(currentStaticStoreId);
                this.mainController.getSDMLogic().generateOrderForStore(
                        storeToOrderFrom, dateOfOrder,
                        lastOrderId, currentOrder.getItemsInOrder(), customer.getLocation());
                this.mainController.getSDMLogic().updateStoreAndSystemItemAmountInformationAccordingToNewOrder(
                        currentOrder.getItemsInOrder(), storeToOrderFrom);
                //Add order to Customer
                this.mainController.getSDMLogic().addStaticOrderToCustomer(
                        currentStaticStoreId, lastOrderId, customer);
                //The amount of orders label is being updated from a bind inside of SDMLogic to orderId
            }
            else {
                double totalDeliveryCost = 0;

                for (Integer storeIdToOrderFrom: dynamicOrder.keySet()) {
                    Store store = this.mainController.getSDMLogic().getStores().get(storeIdToOrderFrom);
                    totalDeliveryCost += store.getDeliveryPpk() * store.calculateDistance(customer.getLocation());
                }

                this.mainController.getSDMLogic().generateDynamicOrderAndRecord(currentOrder.getItemsInOrder(), totalDeliveryCost, dateOfOrder,
                        customer.getLocation(), this.dynamicOrder.keySet().size(), lastOrderId); //Record is only adding dynamic to system dynamic orders.
                this.mainController.getSDMLogic().addDynamicOrderToCustomer(lastOrderId, customer);
                this.dynamicOrder.forEach((storeId, listOfItemsToOrder) -> {
                    Store storeToUpdate = this.mainController.getSDMLogic().getStores().get(storeId);
                    storeToUpdate.generateOrder(dateOfOrder, lastOrderId, listOfItemsToOrder, customer.getLocation() );
                    this.mainController.getSDMLogic().updateStoreAndSystemItemAmountInformationAccordingToNewOrder(listOfItemsToOrder, storeToUpdate);
                });
            }

            showOrderWasSuccessfullyMadeAlert();
            this.currentOrder = new Order();
            resetAllOrder();
        } else {
            displayDateNotChosenError();
        }
    }

    private void showOrderWasSuccessfullyMadeAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order Confirmation");
        alert.setHeaderText(null);
        alert.setContentText("The order was successfully made");
        alert.showAndWait();
    }

    private void displayDateNotChosenError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Date Selection Error");
        alert.setHeaderText(null);
        alert.setContentText("Please select a date in order to finish the order");
        alert.showAndWait();
    }

    public void activateReset() {
        resetAllOrder();
    }
}
