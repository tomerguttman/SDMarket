package controllers;

import SDMImprovedFacade.Discount;
import SDMImprovedFacade.Order;
import SDMImprovedFacade.Store;
import SDMImprovedFacade.StoreItem;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.util.ArrayList;
import java.util.List;

public class OrdersHistoryController {

    private AppController mainController;

    @FXML
    private AnchorPane mainRoot;

    @FXML
    private TableView<Order> ordersHistoryStaticOrdersTableView;

    @FXML
    private TableColumn<Order, Integer> ordersHistoryStaticOrdersTableViewIdColumn;

    @FXML
    private TableColumn<Order, String> ordersHistoryStaticOrdersTableViewDateColumn;

    @FXML
    private TableColumn<Order, Integer> ordersHistoryStaticOrdersTableViewStoreIdColumn;

    @FXML
    private TableColumn<Order, String> ordersHistoryStaticOrdersTableViewStoreNameColumn;

    @FXML
    private TableColumn<Order, Integer> ordersHistoryStaticOrdersTableViewTotalTypesColumn;

    @FXML
    private TableColumn<Order, Double> ordersHistoryStaticOrdersTableViewTotalItemsColumn;

    @FXML
    private TableColumn<Order, Double> ordersHistoryStaticOrdersTableViewItemsCostColumn;

    @FXML
    private TableColumn<Order, Double> ordersHistoryStaticOrdersTableViewDeliveryCostColumn;

    @FXML
    private TableColumn<Order, Double> ordersHistoryStaticOrdersTableViewTotalCostColumn;

    @FXML
    private TableView<Order> ordersHistoryDynamicOrdersTableView;

    @FXML
    private TableColumn<Order, Integer> ordersHistoryDynamicOrdersTableViewIdColumn;

    @FXML
    private TableColumn<Order, String> ordersHistoryDynamicOrdersTableViewDateColumn;

    @FXML
    private TableColumn<Order, Integer> ordersHistoryDynamicOrdersTableViewStoresParticipatingColumn;

    @FXML
    private TableColumn<Order, String> ordersHistoryDynamicOrdersTableViewStoreNameColumn;

    @FXML
    private TableColumn<Order, Integer> ordersHistoryDynamicOrdersTableViewTotalTypesColumn;

    @FXML
    private TableColumn<Order, Double> ordersHistoryDynamicOrdersTableViewTotalItemsColumn;

    @FXML
    private TableColumn<Order, Double> ordersHistoryDynamicOrdersTableViewItemsCostColumn;

    @FXML
    private TableColumn<Order, Double> ordersHistoryDynamicOrdersTableViewDeliveryCostColumn;

    @FXML
    private TableColumn<Order, Double> ordersHistoryDynamicOrdersTableViewTotalCostColumn;

    @FXML
    private TableView<StoreItem> ordersHistoryItemsOfOrderTableView;

    @FXML
    private TableColumn<StoreItem, Integer> ordersHistoryItemsOfOrderTableViewItemIdColumn;

    @FXML
    private TableColumn<StoreItem, String> ordersHistoryItemsOfOrderTableViewNameColumn;

    @FXML
    private TableColumn<StoreItem, Double> ordersHistoryItemsOfOrderTableViewAmountColumn;

    @FXML
    private TableColumn<StoreItem, Double> ordersHistoryItemsOfOrderTableViewPricePerUnitColumn;

    @FXML
    private TableColumn<StoreItem, Double> ordersHistoryItemsOfOrderTableViewTotalPriceColumn;

    @FXML
    private Label ordersHistoryOrderTypeLabel;

    @FXML
    private Label ordersHistoryOrderIdLabel;

    @FXML
    private void initialize(){
        initializeTableViews();
    }

    private void initializeTableViews() {
        initializeStaticOrdersTableView();
        initializeDynamicOrdersTableView();
        initializeItemsOfOrderTableView();
    }

    private void initializeItemsOfOrderTableView() {
        this.ordersHistoryItemsOfOrderTableViewItemIdColumn.setCellValueFactory(new PropertyValueFactory<>("Id"));
        this.ordersHistoryItemsOfOrderTableViewNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        this.ordersHistoryItemsOfOrderTableViewAmountColumn.setCellValueFactory(new PropertyValueFactory<>("totalItemsSold"));
        this.ordersHistoryItemsOfOrderTableViewPricePerUnitColumn.setCellValueFactory(new PropertyValueFactory<>("pricePerUnit"));
        this.ordersHistoryItemsOfOrderTableViewTotalPriceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getTotalPrice()));
    }

    private void initializeDynamicOrdersTableView() {
        this.ordersHistoryDynamicOrdersTableViewIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        this.ordersHistoryDynamicOrdersTableViewDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOrderWasMade"));
        this.ordersHistoryDynamicOrdersTableViewStoresParticipatingColumn.setCellValueFactory(new PropertyValueFactory<>("amountOfStoresRelatedToOrder"));
        this.ordersHistoryDynamicOrdersTableViewStoreNameColumn.setCellValueFactory(new PropertyValueFactory<>("storeName"));
        this.ordersHistoryDynamicOrdersTableViewTotalTypesColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getNumberOfItemsTypesInOrder()));
        this.ordersHistoryDynamicOrdersTableViewTotalItemsColumn.setCellValueFactory(new PropertyValueFactory<>("amountItemsInOrder"));
        this.ordersHistoryDynamicOrdersTableViewItemsCostColumn.setCellValueFactory(new PropertyValueFactory<>("costOfItemsInOrder"));
        this.ordersHistoryDynamicOrdersTableViewDeliveryCostColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryCost"));
        this.ordersHistoryDynamicOrdersTableViewTotalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalOrderCost"));
    }

    private void initializeStaticOrdersTableView() {
        this.ordersHistoryStaticOrdersTableViewIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        this.ordersHistoryStaticOrdersTableViewDateColumn.setCellValueFactory(new PropertyValueFactory<>("dateOrderWasMade"));
        this.ordersHistoryStaticOrdersTableViewStoreIdColumn.setCellValueFactory(new PropertyValueFactory<>("storeId"));
        this.ordersHistoryStaticOrdersTableViewStoreNameColumn.setCellValueFactory(new PropertyValueFactory<>("storeName"));
        this.ordersHistoryStaticOrdersTableViewTotalTypesColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getNumberOfItemsTypesInOrder()));
        this.ordersHistoryStaticOrdersTableViewTotalItemsColumn.setCellValueFactory(new PropertyValueFactory<>("amountItemsInOrder"));
        this.ordersHistoryStaticOrdersTableViewItemsCostColumn.setCellValueFactory(new PropertyValueFactory<>("costOfItemsInOrder"));
        this.ordersHistoryStaticOrdersTableViewDeliveryCostColumn.setCellValueFactory(new PropertyValueFactory<>("deliveryCost"));
        this.ordersHistoryStaticOrdersTableViewTotalCostColumn.setCellValueFactory(new PropertyValueFactory<>("totalOrderCost"));
    }

    @FXML
    void onMouseClickedOrdersTableView(MouseEvent event) {
        try {
            this.ordersHistoryItemsOfOrderTableView.getItems().clear();
            TableView tView = (TableView) event.getSource();
            Order order = (Order) tView.getSelectionModel().getSelectedItem();
            List<StoreItem> itemsInOrder;

            if (order != null) {
                itemsInOrder = new ArrayList<>(order.getItemsInOrder());
                ObservableList<StoreItem> observableDiscountOffersList = FXCollections.observableArrayList();
                observableDiscountOffersList.addAll(itemsInOrder);
                ordersHistoryItemsOfOrderTableView.setItems(observableDiscountOffersList);
                this.ordersHistoryOrderTypeLabel.setText(order.getStoreName().contains("Dynamic") ? "Dynamic" : "Static");
                this.ordersHistoryOrderIdLabel.setText(Integer.toString(order.getOrderId()));
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public void setMainController(AppController mainController){
        this.mainController = mainController;
    }

    public AnchorPane getMainRoot() {
        return this.mainRoot;
    }

    public void loadOrdersToTableViews() {
        this.ordersHistoryStaticOrdersTableView.getItems().clear();
        this.ordersHistoryDynamicOrdersTableView.getItems().clear();
        this.ordersHistoryItemsOfOrderTableView.getItems().clear();

        loadOrdersToStaticOrdersTableView();
        loadOrdersToDynamicOrdersTableView();
    }

    private void loadOrdersToDynamicOrdersTableView() {
        ObservableList<Order> observableOrderList = FXCollections.observableArrayList();
        observableOrderList.addAll(mainController.getSDMLogic().getDynamicOrders().values());
        this.ordersHistoryDynamicOrdersTableView.setItems(observableOrderList);
    }

    private void loadOrdersToStaticOrdersTableView() {
        List<Order> allStaticOrders = new ArrayList<>();

        for (Store store : mainController.getSDMLogic().getStores().values()) {
            allStaticOrders.addAll(store.getStoreOrdersHistory());
        }

        ObservableList<Order> observableOrderList = FXCollections.observableArrayList();
        observableOrderList.addAll(allStaticOrders);
        this.ordersHistoryStaticOrdersTableView.setItems(observableOrderList);
    }

    public void loadDataToLabels() {
    }
}
