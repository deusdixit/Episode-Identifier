package gui.controller;

import gui.models.*;
import gui.tasks.DownloadTask;
import gui.tasks.IdentifyTask;
import gui.tasks.SeasonSearchTask;
import id.gasper.opensubtitles.Opensubtitles;
import id.gasper.opensubtitles.models.authentication.LoginResult;
import id.gasper.opensubtitles.models.features.FeatureQuery;
import io.DataSet;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.SimpleStringProperty;
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
    public Button loadFilesBttn, searchBttn, downloadBttn, anaBttn, renameBttn;

    @FXML
    public ListView<RenamePreviewWrapper> renameList;

    @FXML
    public ListView<RenamePreviewWrapper> previewList;

    @FXML
    public ListView seasonList;
    @FXML
    public TableColumn<TableFeature, String> yearColumn, titleColumn, imdbColumn, seasonColumn, episodeColumn, inDatabaseColumn;
    @FXML
    public TableView osTable;
    @FXML
    TextField usernameField, passwordField, searchField;
    @FXML
    Spinner<Integer> seasonSpinner;

    @FXML
    private DatabaseTabController databaseTabController;

    @FXML
    private ProgressBar progressBar, progressBar2;

    private Stage mainStage;
    private Opensubtitles os = null;
    private final SpinnerValueFactory<Integer> svfSeason = new SpinnerValueFactory.IntegerSpinnerValueFactory(-1, 1000, -1);

    public MainController() {

    }

    public MainController(Stage s) throws IOException {
        mainStage = s;
    }

    public Opensubtitles getOS() {
        if (login()) {
            return os;
        } else {
            return null;
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
                RenamePreviewWrapper rpW = new RenamePreviewWrapper(new RenameItem(f));
                rpW.addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        renameList.refresh();
                        previewList.refresh();
                    }
                });
                renameList.getItems().add(rpW);
            }
        }
        anaBttn.setDisable(renameList.getItems().size() <= 0);
    }

    public void checkLoginAction() {
        login();
    }

    public void searchFieldChanged() {

    }

    public void downloadBttnAction() {
        downloadBttn.setDisable(true);
        searchBttn.setDisable(true);
        DownloadTask task = new DownloadTask(this);
        Thread downloadTask = new Thread(task);
        progressBar2.progressProperty().bind(task.progressProperty());
        downloadTask.setDaemon(true);
        downloadTask.start();
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

    public void renameAction() {
        if (renameList.getItems().size() != previewList.getItems().size()) {
            System.out.println("Error");
        } else {
            for (int i = 0; i < renameList.getItems().size(); i++) {
                File oldFile = renameList.getItems().get(i).getRenameItem().getValue();
                File newFile = new File(previewList.getItems().get(i).getPreviewItem().getSelectedFilename());
                if (oldFile.renameTo(newFile)) {
                    System.out.println("Renamed to " + newFile);
                } else {
                    System.out.println("Error");
                }
            }
            renameList.getItems().clear();
            previewList.getItems().clear();
        }
    }

    public void anaAction() {
        anaBttn.setDisable(true);
        loadFilesBttn.setDisable(true);
        renameBttn.setDisable(true);
        IdentifyTask task = new IdentifyTask(this);
        Thread identTask = new Thread(task);
        progressBar.progressProperty().bind(task.progressProperty());
        identTask.setDaemon(true);
        identTask.start();
    }

    private boolean login() {
        if (os == null && usernameField.getText().length() > 0 && passwordField.getText().length() > 0) {
            os = new Opensubtitles(usernameField.getText(), passwordField.getText(), APIKEY);
        } else return os != null;
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
        seasonSpinner.setValueFactory(svfSeason);
        previewList.setCellFactory(new Callback<ListView<RenamePreviewWrapper>, ListCell<RenamePreviewWrapper>>() {
            @Override
            public ListCell<RenamePreviewWrapper> call(ListView<RenamePreviewWrapper> selectedCandidateListView) {
                return new ListFactoryItem(false);
            }
        });
        renameList.setCellFactory(new Callback<ListView<RenamePreviewWrapper>, ListCell<RenamePreviewWrapper>>() {
            @Override
            public ListCell<RenamePreviewWrapper> call(ListView<RenamePreviewWrapper> renamePreviewWrapperListView) {
                return new ListFactoryItem(true);
            }
        });
        ObservableList<RenamePreviewWrapper> rpList = FXCollections.observableList(new ArrayList<>());
        renameList.setItems(rpList);
        previewList.setItems(rpList);
        databaseTabController.addAll(getDB().get());
        renameList.setFixedCellSize(25);
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
