package controllers;

import SuperMarketLogic.SuperMarketLogic;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.net.URL;

public class AppController {

    private SuperMarketLogic SDMLogic;
    private SimpleBooleanProperty isXMLLoaded;
    private Stage mainStage;

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

    public AppController() {
        isXMLLoaded = new SimpleBooleanProperty(false);
        SDMLogic = new SuperMarketLogic();
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
        /*
        boolean isValidXML;
        StringBuilder sb = new StringBuilder();
        FileChooser dialog = new FileChooser();
        File file = dialog.showOpenDialog(mainStage);
        if( file != null) {
            isValidXML = this.SDMLogic.loadData(file.getAbsolutePath().trim(), sb);
            isXMLLoaded.set( isValidXML || isXMLLoaded.get());
            if(!isValidXML) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("XML loading error");
                alert.setHeaderText(null);
                alert.setContentText(sb.toString());
                alert.showAndWait();
            }
        }
        */
        /*
        window = primaryStage;
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("MainWindowView.fxml"));
        this.root = loader.load();

        MainWindowController mwc = new MainWindowController();
        mwc.setMain(this);

        Scene scene = new Scene(root);
        window.setTitle("JavaFX");
        window.setScene(scene);
        window.show();
        */

        FXMLLoader loader = new FXMLLoader();
        URL mainFXML = getClass().getResource("/fxmls/home/loadXML.fxml");
        loader.setLocation(mainFXML);
        AnchorPane root = loader.load();
        root.setPrefSize(anchorPaneMainWindow.getPrefWidth(), anchorPaneMainWindow.getPrefHeight());
        anchorPaneMainWindow.getChildren().add(root);
    }

    @FXML
    void onActionPurchase(ActionEvent event) {

    }

    @FXML
    void onActionSettings(ActionEvent event) {

    }

    @FXML
    void onActionStores(ActionEvent event) {

    }

    @FXML
    void onActionUpdateInformation(ActionEvent event) {

    }

}
