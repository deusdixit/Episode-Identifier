package gui.controller;

import gui.models.SeasonListViewItem;
import gui.models.TableFeature;
import gui.tasks.DownloadTask;
import gui.tasks.SeasonSearchTask;
import id.gasper.opensubtitles.Opensubtitles;
import id.gasper.opensubtitles.models.authentication.LoginResult;
import id.gasper.opensubtitles.models.features.FeatureQuery;
import io.DataSet;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import utils.Database;
import utils.OsApi;

import java.io.IOException;
import java.util.ArrayList;

public class OpensubtitlesController {

    @FXML
    private Button checkLoginBttn;

    @FXML
    public Button downloadBttn;

    @FXML
    private TableColumn<TableFeature, String> episodeColumn;

    @FXML
    private TableColumn<TableFeature, String> imdbColumn;

    @FXML
    private TableColumn<TableFeature, String> inDatabaseColumn;

    @FXML
    public TableView osTable;

    @FXML
    private PasswordField passwordField;

    @FXML
    private ProgressBar progressBar2;

    @FXML
    public Button searchBttn;

    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<TableFeature, String> seasonColumn;

    @FXML
    public ListView seasonList;

    @FXML
    private Spinner<Integer> seasonSpinner;

    @FXML
    private TableColumn<TableFeature, String> titleColumn;

    @FXML
    private TextField usernameField;

    @FXML
    private TableColumn<TableFeature, String> yearColumn;

    private final SpinnerValueFactory<Integer> svfSeason = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 1000, -1);

    @FXML
    public void downloadBttnAction() {
        downloadBttn.setDisable(true);
        searchBttn.setDisable(true);
        DownloadTask task = new DownloadTask(this);
        Thread downloadTask = new Thread(task);
        progressBar2.progressProperty().bind(task.progressProperty());
        downloadTask.setDaemon(true);
        downloadTask.start();
    }

    @FXML
    public void checkLoginAction() {
        isLoggedIn();
    }

    private boolean isLoggedIn() {
        if (OsApi.getInstance() == null && usernameField.getText().length() > 0 && passwordField.getText().length() > 0) {
            OsApi.setInstance(new Opensubtitles(usernameField.getText(), passwordField.getText(), OsApi.getApiKey()));
        } else {
            return OsApi.getInstance() != null;
        }
        try {
            LoginResult lr = OsApi.getInstance().login();
            if (lr.status == 200) {
                return true;
            } else {
                OsApi.setInstance(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @FXML
    public void searchBttnAction() throws IOException, InterruptedException {
        if (isLoggedIn()) {
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

    @FXML
    void searchFieldChanged(KeyEvent event) {

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

    @FXML
    public void initialize() {
        seasonSpinner.setValueFactory(svfSeason);
        titleColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("title"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("year"));
        imdbColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("imdb"));
        seasonColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("season"));
        episodeColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("episode"));
        inDatabaseColumn.setCellValueFactory(item -> {
            int imdb = item.getValue().imdbProperty().get();
            if (getDB() != null) {
                return new SimpleStringProperty(String.valueOf(getDB().getByImdb(imdb).size()));
            } else {
                return new SimpleStringProperty("0");
            }
        });
    }

}
