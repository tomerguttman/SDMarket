package controllers;

import SDMImprovedFacade.Customer;
import SDMImprovedFacade.Store;
import SDMImprovedFacade.StoreItem;
import SuperMarketLogic.SuperMarketLogic;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AppController {

    private SuperMarketLogic SDMLogic;
    private SimpleBooleanProperty isXMLLoaded;
    private Stage mainStage;

    //Controllers
    private LoadXMLController loadXMLController;
    private StoresController storesController;
    private ItemsController itemsController;
    private CustomersController customersController;
    private Map<Integer, StoreCardController> storeCardControllersMap = new HashMap<>();
    private Map<Integer, CustomerCardController> customerCardControllersMap = new HashMap<>();

    @FXML
    private Button buttonLoadXML;

    @FXML
    private Button buttonPurchase;

    @FXML
    private Button buttonCustomers;

    @FXML
    private Button buttonStores;

    @FXML
    private Button buttonItems;

    @FXML
    private Button buttonSettings;

    @FXML
    private Button buttonUpdateInformation;

    @FXML
    private AnchorPane anchorPaneMainWindow;

    public AppController() throws IOException {
        isXMLLoaded = new SimpleBooleanProperty(false);
        SDMLogic = new SuperMarketLogic();
        loadXMLController = initializeLoadXMLController();
        storesController = initializeStoresController();
        itemsController = initializeItemsController();
        customersController = initializeCustomersController();
    }

    private CustomersController initializeCustomersController() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL mainFXML = getClass().getResource("/fxmls/home/customers.fxml");
        loader.setLocation(mainFXML);
        loader.load();
        CustomersController customersController = loader.getController();
        customersController.setMainController(this);

        return customersController;
    }

    private ItemsController initializeItemsController() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL mainFXML = getClass().getResource("/fxmls/home/items.fxml");
        loader.setLocation(mainFXML);
        loader.load();
        ItemsController itemsController = loader.getController();
        itemsController.setMainController(this);
        return itemsController;
    }

    private StoresController initializeStoresController() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL mainFXML = getClass().getResource("/fxmls/home/stores.fxml");
        loader.setLocation(mainFXML);
        loader.load(); //need to be done before loader.getController() !
        StoresController storesController = loader.getController();
        storesController.setMainController(this);
        /*
            Binds here.
         */
        return storesController;
    }

    private LoadXMLController initializeLoadXMLController() throws IOException {
        FXMLLoader loader = new FXMLLoader();
        URL mainFXML = getClass().getResource("/fxmls/home/loadXML.fxml");
        loader.setLocation(mainFXML);
        loader.load(); //need to be done before loader.getController() !
        LoadXMLController xmlLoadController = loader.getController();
        xmlLoadController.setMainController(this);
        isXMLLoaded.bind(xmlLoadController.getIsXMLLoadedProperty());

        return xmlLoadController;
    }

    public SuperMarketLogic getSDMLogic() {
        return SDMLogic;
    }

    public SimpleBooleanProperty getIsXMLLoaded() {
        return isXMLLoaded;
    }

    public SimpleBooleanProperty isXMLLoadedProperty() {
        return isXMLLoaded;
    }

    public void setIsXMLLoaded(boolean isXMLLoaded) {
        this.isXMLLoaded.set(isXMLLoaded);
    }

    public Stage getMainStage() {
        return mainStage;
    }

    public void setMainStage(Stage mainStage) {
        this.mainStage = mainStage;
    }

    @FXML
    private void initialize() {
        bindButtonsToIsXMLLoaded();
    }

    private void bindButtonsToIsXMLLoaded() {
        buttonCustomers.disableProperty().bind(isXMLLoaded.not());
        buttonItems.disableProperty().bind(isXMLLoaded.not());
        buttonPurchase.disableProperty().bind(isXMLLoaded.not());
        buttonSettings.disableProperty().bind(isXMLLoaded.not());
        buttonStores.disableProperty().bind(isXMLLoaded.not());
        buttonUpdateInformation.disableProperty().bind(isXMLLoaded.not());
    }

    @FXML
    void onActionCustomers(ActionEvent event) {
        try{
            FXMLLoader loader;
            if (!anchorPaneMainWindow.getChildren().contains(customersController.getMainRoot())) {
                anchorPaneMainWindow.getChildren().clear();
                AnchorPane anchorPane = customersController.getMainRoot();
                anchorPaneMainWindow.getChildren().add(anchorPane);
                AnchorPane.setTopAnchor(anchorPane, 0.0);
                AnchorPane.setRightAnchor(anchorPane, 0.0);
                AnchorPane.setBottomAnchor(anchorPane, 0.0);
                AnchorPane.setLeftAnchor(anchorPane, 0.0);

                for(Customer customer : getSDMLogic().getCustomers().values()) {
                    loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/fxmls/home/customerCard.fxml"));
                    loader.load();
                    CustomerCardController customerCardController = loader.getController();
                    customerCardController.setCustomerIdCardLabelText(Integer.toString(customer.getId()));
                    customerCardController.setCustomerNameCardLabelText(customer.getName());
                    customerCardControllersMap.put(customer.getId(), customerCardController);
                }

                customersController.addCustomerCards(customerCardControllersMap);
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    @FXML
    void onActionItems(ActionEvent event) {
        if (!anchorPaneMainWindow.getChildren().contains(itemsController.getRoot())) {
            anchorPaneMainWindow.getChildren().clear();
            AnchorPane itemsControllerMainRoot = itemsController.getRoot();
            anchorPaneMainWindow.getChildren().add(itemsControllerMainRoot);
            AnchorPane.setTopAnchor(itemsControllerMainRoot, 0.0);
            AnchorPane.setRightAnchor(itemsControllerMainRoot, 0.0);
            AnchorPane.setBottomAnchor(itemsControllerMainRoot, 0.0);
            AnchorPane.setLeftAnchor(itemsControllerMainRoot, 0.0);
            itemsController.updateSystemItemsScene(new ArrayList<>(this.getSDMLogic().getItems().values()));
        }
    }

    @FXML
    void onActionLoadXML(ActionEvent event) throws JAXBException, IOException {
        if (!anchorPaneMainWindow.getChildren().contains(loadXMLController.getRoot())) {
            anchorPaneMainWindow.getChildren().clear();
            anchorPaneMainWindow.getChildren().add(loadXMLController.getRoot());
        }
    }

    @FXML
    void onActionPurchase(ActionEvent event) {

    }

    @FXML
    void onActionSettings(ActionEvent event) {

    }

    @FXML
    void onActionStores(ActionEvent event) {
        try{
            FXMLLoader loader;
            if (!anchorPaneMainWindow.getChildren().contains(storesController.getMainRoot())) {
                anchorPaneMainWindow.getChildren().clear();
                AnchorPane anchorPane = storesController.getMainRoot();
                anchorPaneMainWindow.getChildren().add(anchorPane);
                AnchorPane.setTopAnchor(anchorPane, 0.0);
                AnchorPane.setRightAnchor(anchorPane, 0.0);
                AnchorPane.setBottomAnchor(anchorPane, 0.0);
                AnchorPane.setLeftAnchor(anchorPane, 0.0);

                for(Store store : getSDMLogic().getStores().values()) {
                    loader = new FXMLLoader();
                    loader.setLocation(getClass().getResource("/fxmls/home/storeCard.fxml"));
                    loader.load();
                    StoreCardController storeCardController = loader.getController();
                    storeCardController.setStoreIdCardLabelText(Integer.toString(store.getId()));
                    storeCardController.setStoreNameCardLabelText(store.getName());
                    storeCardControllersMap.put(store.getId(), storeCardController);
                }

                storesController.addStoreCards(storeCardControllersMap);
            }
        }
        catch(IOException e){
                e.printStackTrace();
            }
    }

    @FXML
    void onActionUpdateInformation(ActionEvent event) {

    }

}
