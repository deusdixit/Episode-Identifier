package gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

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
    private SettingsController settingsController;

    @FXML
    private TabPane mainTabPane;

    @FXML
    private Tab osTab;

//    private static final Logger log = LoggerFactory.getLogger(MainController.class);


    public MainController() {
    }

/*    public MainController(Stage s) throws IOException {
        mainStage = s;
    }*/

    private static void setInstance(MainController mc) {
        instance = mc;
    }

    public static MainController getInstance() {
        return instance;
    }

    public void setStage(Stage stage) {
        mainStage = stage;
    }

    public Stage getStage() {
        return mainStage;
    }

    public void showOpensubtitles() {
        mainTabPane.getSelectionModel().select(osTab);
        opensubtitlesTabController.usernameField.getStyleClass().add("error");
        opensubtitlesTabController.passwordField.getStyleClass().add("error");
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setInstance(this);
        identifyTabController.setStage(mainStage);
        databaseTabController.setStage(mainStage);
    }
}
