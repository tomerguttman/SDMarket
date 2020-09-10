package controllers;

import SDMImprovedFacade.Customer;
import SDMImprovedFacade.Order;
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

import java.util.List;
import java.util.Map;

public class CustomersController {
    AppController mainController;

    @FXML
    private AnchorPane mainRoot;

    @FXML
    private ScrollPane storesScrollPane;

    @FXML
    private VBox customerCardsVBox;

    @FXML
    private TableView<Order> customerOrdersTableView;

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
    private Label locationLabel;

    @FXML
    private Label numberOfOrdersLabel;

    @FXML
    private Label averagePriceOfDeliveryLabel;

    @FXML
    private Label averageCartPriceLabel;

    @FXML
    private Label customerHeaderLabel;

    @FXML
    private void initialize() {
        setOrdersTableColumnsProperties();
    }

    private void setOrdersTableColumnsProperties() {
        this.ordersTableViewDateColumn.setCellValueFactory(new PropertyValueFactory<Order, String>("dateOrderWasMade"));
        this.ordersTableViewDeliveryCostColumn.setCellValueFactory(new PropertyValueFactory<Order, Double>("deliveryCost"));
        this.ordersTableViewIdColumn.setCellValueFactory(new PropertyValueFactory<Order, Integer>("orderId"));
        this.ordersTableViewTotalItemsPriceColumn.setCellValueFactory(new PropertyValueFactory<Order, Double>("costOfItemsInOrder"));
        this.ordersTableViewTotalItemsColumn.setCellValueFactory(new PropertyValueFactory<Order, Integer>("amountItemsInOrder"));
        this.ordersTableViewTotalPriceColumn.setCellValueFactory(new PropertyValueFactory<Order, Double>("totalOrderCost"));
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    public void addCustomerCards(Map<Integer, CustomerCardController> customerCardControllerMap) {
        this.customerCardsVBox.getChildren().clear();

        for (CustomerCardController customerCard : customerCardControllerMap.values()) {
            customerCardsVBox.getChildren().add(customerCard.getMainRoot());
            customerCard.getMainRoot().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    AnchorPane customerCard = (AnchorPane) event.getSource();
                    Pane pane = (Pane)customerCard.getChildren().get(0);
                    int customerId = Integer.parseInt(((Label)pane.getChildren().get(1)).textProperty().get());
                    Customer customer = mainController.getSDMLogic().getCustomers().get(customerId);
                    updateCustomersView(customer);
                }
            });
        }
    }

    private void updateCustomersView(Customer customer) {
        updateCustomerHeader(customer);
        updateOrdersTableView(customer.getCustomerOrders());
    }

    private void updateCustomerHeader(Customer customer) {
        try {
            this.averageCartPriceLabel.setText(String.format("%.2f", customer.getAverageOrdersPriceWithoutDelivery()));
            this.averagePriceOfDeliveryLabel.setText(String.format("%.2f", customer.getAverageOrdersDeliveryPrice()));
            this.locationLabel.setText(String.format("(%d,%d)", customer.getLocation().getX(), customer.getLocation().getY()));
            this.numberOfOrdersLabel.setText(Integer.toString(customer.getTotalNumberOfOrders()));
            this.customerHeaderLabel.setText(String.format("%d : %s", customer.getId(), customer.getName()));
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void updateOrdersTableView(List<Order> customerOrdersHistory) {
        try {
            this.customerOrdersTableView.getItems().clear();
            ObservableList<Order> observableOrdersList = FXCollections.observableArrayList();
            observableOrdersList.addAll(customerOrdersHistory);
            customerOrdersTableView.setItems(observableOrdersList);
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    public AnchorPane getMainRoot() {
        return mainRoot;
    }
}
