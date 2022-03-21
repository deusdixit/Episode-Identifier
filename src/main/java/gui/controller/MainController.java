package gui.controller;

import gui.models.ListFactoryItem;
import gui.models.RenameItem;
import gui.models.RenamePreviewWrapper;
import gui.tasks.IdentifyTask;
import io.DataSet;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
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


    @FXML
    public Button loadFilesBttn, anaBttn, renameBttn;

    @FXML
    public ListView<RenamePreviewWrapper> renameList;

    @FXML
    public ListView<RenamePreviewWrapper> previewList;

    @FXML
    private ProgressBar progressBar;

    private Stage mainStage;

    @FXML
    private DatabaseTabController databaseTabController;


    public MainController() {

    }

    public MainController(Stage s) throws IOException {
        mainStage = s;
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

    }
}
