package gui.controller;

import gui.components.AccountDetails;
import gui.components.ImageButton;
import gui.exceptions.NoOpensubtitlesException;
import gui.models.TreeItemWrapper;
import gui.tasks.DownloadTask;
import gui.tasks.SeasonSearchTask;
import id.gasper.opensubtitles.Opensubtitles;
import id.gasper.opensubtitles.models.authentication.LoginResult;
import id.gasper.opensubtitles.models.features.FeatureQuery;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.OsApi;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Objects;
import java.util.prefs.Preferences;

public class OpensubtitlesTabController {

    @FXML
    public PasswordField passwordField;

    @FXML
    public ProgressBar progressBar2;

    @FXML
    public Button searchBttn;

    @FXML
    public TextField searchField;

    @FXML
    public TreeTableView<TreeItemWrapper> dataTTview;

    @FXML
    private TreeTableColumn<TreeItemWrapper, ImageButton> downloadTTColumn;

    @FXML
    private TreeTableColumn<TreeItemWrapper, String> itemTTColumn;

    public TreeItem<TreeItemWrapper> ttRoot;

    @FXML
    public ImageView thumbnail;

    @FXML
    public TextField usernameField;

    @FXML
    public Label numDbLabel, imdbLabel, tmdbLabel, yearLabel;

    @FXML
    public TextField imdbField;

    @FXML
    public AccountDetails accountDetails;

    //private LinkedList<ImageButton> allButtons;

    private Preferences prefs;

    private boolean downloadTaskRunning = false;

    private static final Logger log = LoggerFactory.getLogger(OpensubtitlesTabController.class);

    @FXML
    public void checkLoginAction() {
        isLoggedIn();
    }

    private boolean isLoggedIn() {
        try {
            prefs.put("username", usernameField.getText());
            prefs.put("password", passwordField.getText());
            if (usernameField.getText().length() > 0 && passwordField.getText().length() > 0) {
                OsApi.setInstance(new Opensubtitles(usernameField.getText(), passwordField.getText(), OsApi.getApiKey()));
            } else {
                return OsApi.getInstance() != null;
            }

            LoginResult lr = OsApi.getInstance().login();
            accountDetails.set(lr);
            if (lr.status == 200) {
                return true;
            } else {
                OsApi.setInstance(null);
                log.error("Login failed with Error Code " + lr.status);
            }
        } catch (IOException e) {
            log.error("IOException isLoggedIn()");
        } catch (InterruptedException e) {
            log.error("InterruptedException isLoggedIn()");
        } catch (NoOpensubtitlesException noe) {
            noe.getMainController().showOpensubtitles();
        }
        return false;
    }

    @FXML
    public void searchBttnAction() {
        try {
            if (isLoggedIn()) {
                String value = searchField.getText();
                ttRoot.getChildren().clear();
                FeatureQuery fq = new FeatureQuery().setQuery(value);
                fq.setType(FeatureQuery.Type.TVSHOW);

                SeasonSearchTask task = new SeasonSearchTask(value, this, OsApi.getInstance());
                Thread getSeasonThread = new Thread(task);
                getSeasonThread.setDaemon(true);
                getSeasonThread.start();

            }
        } catch (NoOpensubtitlesException noe) {
            noe.getMainController().showOpensubtitles();
        }
    }

    @FXML
    void searchFieldChanged() {
        searchBttn.setDisable(searchField.getText().length() <= 0);
    }

    @FXML
    void parentImdbChanged() {
        searchBttn.setDisable(imdbField.getText().length() <= 0 || !imdbField.getText().matches("\\d+"));
    }

    public void setDisableDownloadButtons(boolean value) {
        log.debug("Set Button Disable : " + value);
        downloadTaskRunning = value;
        ImageButton imgb;
        int counter = 0;
        while ((imgb = downloadTTColumn.getCellData(counter)) != null) {
            imgb.setDisable(value);
            counter++;
            log.debug("Set " + value);
        }
        dataTTview.refresh();
    }

    private void setPreferences() {
        prefs = Preferences.userRoot().node(getClass().getName());
        usernameField.setText(prefs.get("username", ""));
        passwordField.setText(prefs.get("password", ""));
    }

    private void download(TreeItem<TreeItemWrapper> item) {
        LinkedList<TreeItemWrapper> list = new LinkedList<>();
        if (item.getValue().type == TreeItemWrapper.Type.SEASON) {
            for (TreeItem<TreeItemWrapper> tw : item.getChildren()) {
                list.add(tw.getValue());
            }
        } else if (item.getValue().type == TreeItemWrapper.Type.EPISODE) {
            list.add(item.getValue());
        }
        searchBttn.setDisable(true);
        DownloadTask task = new DownloadTask(this, list);
        Thread downloadTask = new Thread(task);
        progressBar2.progressProperty().bind(task.progressProperty());
        downloadTask.setDaemon(true);
        downloadTask.start();
    }

    @FXML
    public void initialize() {
        setPreferences();
        searchBttn.setDisable(true);
        //allButtons = new LinkedList<>();
        dataTTview.setShowRoot(false);
        ttRoot = new TreeItem<>();
        dataTTview.setRoot(ttRoot);
        itemTTColumn.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        downloadTTColumn.setCellValueFactory(new Callback<>() {
            @Override
            public ObservableValue<ImageButton> call(TreeTableColumn.CellDataFeatures<TreeItemWrapper, ImageButton> item) {
                ImageButton imgBttn = new ImageButton();
                imgBttn.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/download.png"))), 20, 20);
                imgBttn.setOnAction(actionEvent -> download(item.getValue()));
                if (item.getValue().getValue().type == TreeItemWrapper.Type.TVSHOW) {
                    imgBttn.setVisible(false);
                    imgBttn.setDisable(true);
                } else {
                    imgBttn.setDisable(downloadTaskRunning);
                    //allButtons.add(imgBttn);
                }
                return new SimpleObjectProperty<>(imgBttn);
            }
        });

        dataTTview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends TreeItem<TreeItemWrapper>> observableValue, TreeItem<TreeItemWrapper> oldItem, TreeItem<TreeItemWrapper> newItem) {
                if (newItem.getValue().getImgUrl().length() > 0) {
                    thumbnail.setImage(new Image(newItem.getValue().getImgUrl()));
                } else {
                    thumbnail.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/empty.png"))));
                }
                numDbLabel.setText(newItem.getValue().getNumDb());
                imdbLabel.setText(newItem.getValue().getImdb());
                tmdbLabel.setText(newItem.getValue().getTmdb());
                yearLabel.setText(newItem.getValue().getYear());
            }
        });
        thumbnail.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/empty.png"))));
        usernameField.textProperty().addListener((event) -> usernameField.getStyleClass().remove("error"));
        passwordField.textProperty().addListener((event) -> passwordField.getStyleClass().remove("error"));
    }

}
