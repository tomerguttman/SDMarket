package controllers;

import SuperMarketLogic.SuperMarketLogic;
import javafx.beans.property.SimpleBooleanProperty;
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
import java.util.List;

public class AppController {

    private SuperMarketLogic SDMLogic;
    private SimpleBooleanProperty isXMLLoaded;
    private Stage mainStage;

    //Controllers
    private LoadXMLController loadXMLController;
    private StoresController storesController;

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
        System.out.println("check check!");
    }

    @FXML
    void onActionItems(ActionEvent event) {

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
        if (!anchorPaneMainWindow.getChildren().contains(storesController.getMainRoot())) {
            anchorPaneMainWindow.getChildren().clear();
            AnchorPane anchorPane = storesController.getMainRoot();
            anchorPaneMainWindow.getChildren().add(anchorPane);
            AnchorPane.setTopAnchor(anchorPane,0.0);
            AnchorPane.setRightAnchor(anchorPane,0.0);
            AnchorPane.setBottomAnchor(anchorPane,0.0);
            AnchorPane.setLeftAnchor(anchorPane,0.0);

            List<StoreCardController> storeCards = new ArrayList<>();

            this.getSDMLogic().getStores().values().forEach(store -> {
                StoreCardController storeCard = new StoreCardController();
                storeCard.setStoreIdCardLabelText(Integer.toString(store.getId()));
                storeCard.setStoreNameCardLabelText(store.getName());
                storeCards.add(storeCard);
            });

            storesController.addStoreCards(storeCards);
            //D:\Java - SDM\SDM_ConsoleApp\src\tests

        }
    }

    @FXML
    void onActionUpdateInformation(ActionEvent event) {

    }

}
