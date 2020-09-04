package controllers;

import SDMImprovedFacade.Discount;
import SDMImprovedFacade.Order;
import SDMImprovedFacade.Store;
import SDMImprovedFacade.StoreItem;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StoresController {
    private AppController mainController;

    @FXML
    private AnchorPane mainRoot;

    @FXML
    private VBox storeCardsVBox;

    @FXML
    private ScrollPane storesScrollPane;

    @FXML
    private TableView<StoreItem> itemsTableView;

    @FXML
    private TableColumn<StoreItem, Integer> itemsTableViewIdColumn;

    @FXML
    private TableColumn<StoreItem, String> itemsTableViewNameColumn;

    @FXML
    private TableColumn<StoreItem, String> itemsTableViewCategoryColumn;

    @FXML
    private TableColumn<StoreItem, Double> itemsTableViewPriceColumn;

    @FXML
    private TableColumn<StoreItem, Double> itemsTableViewAmountSoldColumn;

    @FXML
    private TableView<Order> ordersTableView;

    @FXML
    private TableColumn<Order, Integer> ordersTableViewIdColumn;

    @FXML
    private TableColumn<Order, String> ordersTableViewDateColumn;

    @FXML
    private TableColumn<Order, Integer> ordersTableViewTotalItemsColumn;

    @FXML
    private TableColumn<Order, Double> ordersTableViewTotalItemsPriceColumn;

    @FXML
    private TableColumn<Order, Double> ordersTableViewDeliveryCostColumn;

    @FXML
    private TableColumn<Order, Double> ordersTableViewTotalPriceColumn;

    @FXML
    private TableView<Discount> discountsTableView;

    @FXML
    private TableColumn<Discount, String> discountsTableViewNameColumn;

    @FXML
    private TableColumn<Discount, String> discountsTableViewIfYouBuyColumn;

    @FXML
    private TableColumn<Discount, Double> discountsTableViewQuantityNeededColumn;

    @FXML
    private TableColumn<Discount, String> discountsTableViewThenYouGetColumn;

    @FXML
    private Label ppkLabel;

    @FXML
    private Label totalRevenueLabel;

    @FXML
    private Label salesLabel;

    @FXML
    private Label itemsLabel;

    @FXML
    private Label storesHeaderLabel;

    @FXML
    private TableView<?> thenYouGetTableView;

    @FXML
    private TableColumn<?, ?> thenYouGetTableViewItemNameColumn;

    @FXML
    private TableColumn<?, ?> thenYouGetTableViewQuantityColumn;

    @FXML
    private TableColumn<?, ?> thenYouGetTableViewForAdditionalColumn;

    @FXML
    private void initialize() {
        setItemsTableColumnsProperties();
        setOrdersTableColumnsProperties();
        setDiscountsTableColumnsProperties();
        setThenYouGetTableColumnsProperties();
    }

    private void setThenYouGetTableColumnsProperties() {
    }

    private void setDiscountsTableColumnsProperties() {
        this.discountsTableViewIfYouBuyColumn.setCellValueFactory(new PropertyValueFactory<Discount, String>("itemToBuyName"));
        this.discountsTableViewNameColumn.setCellValueFactory(new PropertyValueFactory<Discount, String>("name"));
        this.discountsTableViewQuantityNeededColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<Double>(cellData.getValue().getBuyThis().getQuantity()));
        this.discountsTableViewThenYouGetColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getGetThat().getOperator()));
        //ordersTableView.getSelectionModel().getSelectedItem()
    }

    private void setOrdersTableColumnsProperties() {
        this.ordersTableViewDateColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("dateOrderWasMade"));
        this.ordersTableViewDeliveryCostColumn.setCellValueFactory(new PropertyValueFactory<Order, Double>("deliveryCost"));
        this.ordersTableViewIdColumn.setCellValueFactory(new PropertyValueFactory<Order, Integer>("orderId"));
        this.ordersTableViewTotalItemsPriceColumn.setCellValueFactory(new PropertyValueFactory<Order, Double>("costOfItemsInOrder"));
        this.ordersTableViewTotalItemsColumn.setCellValueFactory(new PropertyValueFactory<Order, Integer>("amountItemsInOrder"));
        this.ordersTableViewTotalPriceColumn.setCellValueFactory(new PropertyValueFactory<Order, Double>("totalOrderCost"));
    }

    private void setItemsTableColumnsProperties() {
        this.itemsTableViewIdColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Integer>("Id"));
        this.itemsTableViewAmountSoldColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Double>("totalItemsSold"));
        this.itemsTableViewCategoryColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, String>("purchaseCategory"));
        this.itemsTableViewNameColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, String>("name"));
        this.itemsTableViewPriceColumn.setCellValueFactory(new PropertyValueFactory<StoreItem, Double>("pricePerUnit"));
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public AnchorPane getMainRoot() {
        return mainRoot;
    }

    public void addStoreCards(Map<Integer, StoreCardController> storeCardControllersMap) {
        storeCardsVBox.getChildren().clear();

        for (StoreCardController storeCard : storeCardControllersMap.values()) {
            storeCardsVBox.getChildren().add(storeCard.getMainRoot());
            storeCard.getMainRoot().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    AnchorPane storeCard = (AnchorPane) event.getSource(); //?
                    Pane pane = (Pane)storeCard.getChildren().get(0);
                    int storeId = Integer.parseInt(((Label)pane.getChildren().get(1)).textProperty().get());
                    Store store = mainController.getSDMLogic().getStores().get(storeId);
                    updateStoresView(store);
                }
            });
        }
    }

    private void updateStoresView(Store store) {
        updateStoresHeader(store);
        updateItemsTableView(store.getItemsBeingSold());
        updateOrdersTableView(store.getStoreOrdersHistory());
        updateDiscountsTableView(store.getStoreDiscounts());
    }

    private void updateDiscountsTableView(Map<Integer, List<Discount>> storeDiscounts) {
        try {
            List<Discount> allDiscounts = new ArrayList<>();
            for (List<Discount> currentList : storeDiscounts.values()) {
                allDiscounts.addAll(currentList);
            }
            this.discountsTableView.getItems().clear();
            ObservableList<Discount> observableDiscountsList = FXCollections.observableArrayList();
            observableDiscountsList.addAll(allDiscounts);
            discountsTableView.setItems(observableDiscountsList);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void updateOrdersTableView(List<Order> storeOrdersHistory) {
        try {
            this.ordersTableView.getItems().clear();
            ObservableList<Order> observableOrdersList = FXCollections.observableArrayList();
            observableOrdersList.addAll(storeOrdersHistory);
            ordersTableView.setItems(observableOrdersList);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void updateItemsTableView(Map<Integer, StoreItem> itemsBeingSold) {
        try {
            this.itemsTableView.getItems().clear();
            ObservableList<StoreItem> observableStoreItemsList = FXCollections.observableArrayList();
            observableStoreItemsList.addAll(itemsBeingSold.values());
            itemsTableView.setItems(observableStoreItemsList);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void updateStoresHeader(Store store) {
        try {
            this.itemsLabel.setText(Integer.toString(store.getItemsBeingSold().size()));
            this.ppkLabel.setText(Integer.toString(store.getDeliveryPpk()));
            //this.salesLabel.setText(Integer.toString(store.getSales().size()));
            this.totalRevenueLabel.setText(Double.toString(store.getTotalOrdersRevenue()));
            this.storesHeaderLabel.setText(String.format("%d : %s", store.getId(), store.getName()));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @FXML
    void onMouseClickedDiscountsTableView(MouseEvent event) {

    }
}
