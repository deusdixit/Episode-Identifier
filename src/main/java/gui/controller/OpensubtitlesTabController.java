package gui.controller;

import gui.components.ImageButton;
import gui.models.SeasonListViewItem;
import gui.models.TableFeature;
import gui.tasks.DownloadTask;
import gui.tasks.SeasonSearchTask;
import gui.tasks.UglyTask;
import id.gasper.opensubtitles.Opensubtitles;
import id.gasper.opensubtitles.models.authentication.LoginResult;
import id.gasper.opensubtitles.models.features.FeatureQuery;
import io.DataSet;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.util.Callback;
import utils.Database;
import utils.OsApi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class OpensubtitlesTabController {

    @FXML
    private Button checkLoginBttn;

    @FXML
    public ImageButton downloadBttn;

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
    public ProgressBar progressBar2;

    @FXML
    public Button searchBttn;

    @FXML
    public TextField searchField;

    @FXML
    private TableColumn<TableFeature, String> seasonColumn;

    @FXML
    public ListView seasonList;

    @FXML
    public Spinner<Integer> seasonSpinner;

    @FXML
    private TableColumn<TableFeature, String> titleColumn;

    @FXML
    private TextField usernameField;

    @FXML
    private TableColumn<TableFeature, String> yearColumn;

    @FXML
    CheckBox episodeCheckbox;

    @FXML
    public TextField imdbField;

    @FXML
    public ImageButton removeAllBttn;

    @FXML
    public ImageButton removeBttn;

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
            if (episodeCheckbox.isSelected() && seasonSpinner.getValue() > 0) {
                UglyTask task = new UglyTask(this);
                Thread uglythread = new Thread(task);
                uglythread.setDaemon(true);
                uglythread.start();
            } else {
                FeatureQuery fq = new FeatureQuery().setQuery(value);
                fq.setType(FeatureQuery.Type.TVSHOW);
                SeasonSearchTask task = new SeasonSearchTask(value, -1, this);
                Thread getSeasonThread = new Thread(task);
                getSeasonThread.setDaemon(true);
                getSeasonThread.start();
            }
        }
    }

    @FXML
    public void removeAllAction() {
        osTable.getItems().clear();
    }

    @FXML
    void searchFieldChanged(KeyEvent event) {

    }

    @FXML
    public void removeBttnAction() {
        ArrayList<TablePosition> tp = new ArrayList<>(osTable.getSelectionModel().getSelectedCells());
        Collections.sort(tp, (x, y) -> -Integer.compare(x.getRow(), y.getRow()));
        for (TablePosition t : tp) {
            osTable.getItems().remove(t.getRow());
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

    @FXML
    public void initialize() {
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
        ObservableList<TableFeature> liste = FXCollections.observableList(new ArrayList<>());
        seasonList.setItems(liste);
        osTable.getItems().addListener(new ListChangeListener<TableFeature>() {
            @Override
            public void onChanged(Change<? extends TableFeature> change) {
                if (change.getList().size() > 0) {
                    downloadBttn.setDisable(false);
                    removeAllBttn.setDisable(false);
                    removeBttn.setDisable(false);
                } else {
                    downloadBttn.setDisable(true);
                    removeAllBttn.setDisable(true);
                    removeBttn.setDisable(true);
                }
            }
        });
        seasonSpinner.setValueFactory(svfSeason);
        titleColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("title"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("year"));
        imdbColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("imdb"));
        seasonColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("season"));
        episodeColumn.setCellValueFactory(new PropertyValueFactory<TableFeature, String>("episode"));
        osTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        inDatabaseColumn.setCellValueFactory(item -> {
            int imdb = item.getValue().imdbProperty().get();
            if (getDB() != null) {
                return new SimpleStringProperty(String.valueOf(getDB().getByImdb(imdb).size()));
            } else {
                return new SimpleStringProperty("0");
            }
        });
        removeAllBttn.setImage(new Image(getClass().getResourceAsStream("/icons/remove_all.png")), 20, 20);
        removeBttn.setImage(new Image(getClass().getResourceAsStream("/icons/remove.png")), 20, 20);
        downloadBttn.setImage(new Image(getClass().getResourceAsStream("/icons/download.png")), 70, 70);
    }

}
