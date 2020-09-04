package controllers;

import SuperMarketLogic.SuperMarketLogic;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.When;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;

import javax.xml.bind.JAXBException;
import java.io.File;

public class LoadXMLController {

    private AppController mainController;
    private SimpleBooleanProperty isXMLTaskLoaded;

    public LoadXMLController() {
        isXMLTaskLoaded = new SimpleBooleanProperty(false);
    }

    @FXML
    private AnchorPane mainRoot;

    @FXML
    private Button buttonChooseFile;

    @FXML
    private Button buttonLoadFile;

    @FXML
    private ProgressBar loadXMLProgressBar;

    @FXML
    private Label pathToFileLabel;

    @FXML
    private Label amountOrdersLabel;

    @FXML
    private Label amountCustomersLabel;

    @FXML
    private Label amountStoresLabel;

    @FXML
    private Label amountItemsLabel;


    @FXML
    void onActionChooseXMLFile(ActionEvent event) throws JAXBException {
        FileChooser dialog = new FileChooser();
        File file = dialog.showOpenDialog(mainController.getMainStage());

        if( file != null) {
            this.pathToFileLabel.textProperty().set(file.getAbsolutePath());
        }
    }

    public void setMainController(AppController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void onActionLoadXML(ActionEvent event) throws Exception {

        LoadXMLTask task = new LoadXMLTask();

        if(!this.pathToFileLabel.textProperty().get().equals("-"))
        {
            //isXMLTaskLoaded.bind(Bindings.when(task.valueProperty().get() || isXMLTaskLoaded.get());
            isXMLTaskLoaded.set(true);
            loadXMLProgressBar.progressProperty().bind(task.progressProperty());

            Runnable target = new Runnable() {
                @Override
                public void run() {
                    try {
                        task.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };
            Thread th = new Thread(target);
            th.start();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("XML loading error");
            alert.setHeaderText(null);
            alert.setContentText("Please choose a file first");
            alert.showAndWait();
        }
    }

    public AnchorPane getRoot() {
        return this.mainRoot;
    }

    public ObservableValue<? extends Boolean> getIsXMLLoadedProperty() {
        return isXMLTaskLoaded;
    }

    private class LoadXMLTask extends Task<Boolean> {
        @Override
        protected Boolean call() throws Exception {
            StringBuilder sb = new StringBuilder();
            File file = new File(pathToFileLabel.textProperty().get().trim());
            boolean isValidXML = mainController.getSDMLogic().loadData(file.getAbsolutePath().trim(), sb);

            //mainController.getIsXMLLoaded().set( isValidXML || mainController.getIsXMLLoaded().get());
            delayProgress();
            if(!isValidXML && !isXMLTaskLoaded.get()) {
                Platform.runLater(() -> {
                    loadXMLProgressBar.setStyle("-fx-accent: red;");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("XML loading error");
                    alert.setHeaderText(null);
                    alert.setContentText(sb.toString());
                    alert.showAndWait();
                });
            }
            else if(isValidXML && sb.length() > 0){
                Platform.runLater(() -> {
                    loadXMLProgressBar.setStyle("-fx-accent: red;");
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("XML loading error");
                    alert.setHeaderText("The previous XML file is still loaded due to an error in the loading process of the new one");
                    alert.setContentText("Error detected: " + sb.toString());
                    alert.showAndWait();
                });
            }
            else{
                Platform.runLater(() -> {
                    amountCustomersLabel.textProperty().bind(mainController.getSDMLogic().getAmountCustomers());
                    amountStoresLabel.textProperty().bind(mainController.getSDMLogic().getAmountStoresStringProperty());
                    amountItemsLabel.textProperty().bind(mainController.getSDMLogic().getAmountItemsStringProperty());
                    amountOrdersLabel.textProperty().bind(mainController.getSDMLogic().getAmountOrdersStringProperty());
                    loadXMLProgressBar.setStyle("-fx-accent: green;");
                });
            }

            return Boolean.TRUE;
        }

        private void delayProgress() throws InterruptedException {
            loadXMLProgressBar.setStyle("-fx-accent: blue;");
            for (int i = 1; i <= 1000; i++) {
                this.updateProgress(i,1000);
                Thread.sleep(3);
            }
        }
    }
}

