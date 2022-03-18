package gui.controller;

import gui.models.SeasonListViewItem;
import gui.models.TableFeature;
import gui.tasks.SeasonSearchTask;
import id.gasper.opensubtitles.Opensubtitles;
import id.gasper.opensubtitles.models.authentication.LoginResult;
import id.gasper.opensubtitles.models.features.FeatureQuery;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import utils.Database;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    private final String APIKEY = "9t8TuCJNE6AUBw0M7tlYDUVpmtwSHH8L";

    @FXML
    public Button loadFilesBttn, searchBttn, downloadBttn;

    @FXML
    public ListView renameList, previewList, seasonList;

    @FXML
    TextField usernameField, passwordField, searchField;

    @FXML
    public TableColumn<TableFeature, String> yearColumn, titleColumn, imdbColumn, seasonColumn, episodeColumn;

    @FXML
    public TableView osTable;

    @FXML
    Spinner<Integer> episodeSpinner, seasonSpinner;

    @FXML
    private DatabaseTabController databaseTabController;

    private Stage mainStage;
    private Opensubtitles os = null;
    private SpinnerValueFactory<Integer> svfEpisode = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 1000, -1);
    private SpinnerValueFactory<Integer> svfSeason = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 1000, -1);

    public MainController() {

    }

    public MainController(Stage s) throws IOException {
        mainStage = s;
    }

    public Opensubtitles getOS() {
        return os;
    }

    public void setStage(Stage stage) {
        mainStage = stage;
    }

    public void loadFilesAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Video Files", "*.mkv", "*.mp4", "*.avi", "*.MKV", "*.MP4", "*.AVI"), new FileChooser.ExtensionFilter("All Files", "*.*"));
        List<File> files = fileChooser.showOpenMultipleDialog(mainStage);
        if (files != null && files.size() > 0) {
            for (File f : files) {
                renameList.getItems().add(f);
            }
        }
    }

    public void checkLoginAction() {
        login();
    }

    public void searchFieldChanged() {

    }

    public void downloadBttnAction() {
        ObservableList<TableFeature> list = osTable.getItems();
        if (!list.isEmpty()) {
            for (TableFeature tf : list) {
                try {
                    Database.downloadAutomatic(getOS(), tf.imdbProperty().getValue());
                } catch (IOException ioe) {

                } catch (ClassNotFoundException cnfe) {

                } catch (InterruptedException ie) {

                }
            }
        }
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

    private boolean login() {
        if (os == null) {
            os = new Opensubtitles(usernameField.getText(), passwordField.getText(), APIKEY);
        } else {
            return true;
        }
        try {
            LoginResult lr = os.login();
            if (lr.status == 200) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void searchBttnAction() throws IOException, InterruptedException {
        if (login()) {
            titleColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("title"));
            yearColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("year"));
            imdbColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("imdb"));
            seasonColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("season"));
            episodeColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("episode"));
            String value = searchField.getText();
            FeatureQuery fq = new FeatureQuery().setQuery(value).setType(FeatureQuery.Type.TVSHOW);
            if (seasonSpinner.getValue() >= 0) {

            }
            ObservableList<TableFeature> liste = FXCollections.observableList(new ArrayList<>());
            seasonList.setCellFactory(new Callback<ListView<SeasonListViewItem>, ListCell<SeasonListViewItem>>() {
                @Override
                public ListCell<SeasonListViewItem> call(ListView<SeasonListViewItem> seasonListViewItemListView) {
                    return new ListCell<>() {
                        @Override
                        public void updateItem(SeasonListViewItem item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                setText(null);
                                setGraphic(item);
                            }
                        }
                    };
                }
            });
            seasonList.setItems(liste);
            SeasonSearchTask task = new SeasonSearchTask(value, -1, this);
            Thread getEpisodesThread = new Thread(task);
            getEpisodesThread.setDaemon(true);
            getEpisodesThread.start();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        episodeSpinner.setValueFactory(svfEpisode);
        seasonSpinner.setValueFactory(svfSeason);
    }
}
