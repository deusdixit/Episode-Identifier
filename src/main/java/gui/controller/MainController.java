package gui.controller;

import io.DataSet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Database;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    private Stage mainStage;

    private static MainController instance = null;

    @FXML
    private DatabaseTabController databaseTabController;

    @FXML
    private IdentifyTabController identifyTabController;

    @FXML
    private OpensubtitlesTabController opensubtitlesTabController;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab osTab;

    private static final Logger log = LoggerFactory.getLogger(MainController.class);


    public MainController() {
    }

    public MainController(Stage s) throws IOException {
        mainStage = s;
    }

    private static void setInstance(MainController mc) {
        instance = mc;
    }

    public static MainController getInstance() {
        return instance;
    }

    public void setStage(Stage stage) {
        mainStage = stage;
    }

    public void showOpensubtitles() {
        mainTabPane.getSelectionModel().select(osTab);
        opensubtitlesTabController.usernameField.getStyleClass().add("error");
        opensubtitlesTabController.passwordField.getStyleClass().add("error");
    }


    public DataSet getDB() {
        try {
            return Database.getDatabase();
        } catch (IOException ioe) {
            log.error("IOException getDB()");
        } catch (ClassNotFoundException cnfe) {
            log.error("ClassNotFoundException getDB()");
        }
        return null;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setInstance(this);
        identifyTabController.setStage(mainStage);
        databaseTabController.setStage(mainStage);
    }
}
