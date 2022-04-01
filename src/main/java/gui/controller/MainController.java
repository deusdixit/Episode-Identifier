package gui.controller;

import io.DataSet;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import utils.Database;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {


    private Stage mainStage;

    @FXML
    private DatabaseTabController databaseTabController;

    @FXML
    private IdentifyTabController identifyTabController;

    @FXML
    private OpensubtitlesTabController opensubtitlesTabController;


    public MainController() {

    }

    public MainController(Stage s) throws IOException {
        mainStage = s;
    }



    public void setStage(Stage stage) {
        mainStage = stage;
    }


    public void loadMenuAction() {
        try {
            Database.loadSubtitles();
            databaseTabController.clear();
            databaseTabController.addAll(Database.getDatabase().get());
        } catch (IOException iex) {

        } catch (ClassNotFoundException cnfe) {

        }
    }



    public DataSet getDB() {
        try {
            return Database.getDatabase();
        } catch (IOException ioe) {
            System.out.println();
        } catch (ClassNotFoundException cnfe) {
            System.out.println();
        }
        return null;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        identifyTabController.setStage(mainStage);
    }
}
