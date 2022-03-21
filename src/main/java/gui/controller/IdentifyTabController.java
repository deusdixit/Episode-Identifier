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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.Database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IdentifyTabController {

    @FXML
    public Button anaBttn;

    @FXML
    public Button loadFilesBttn;

    @FXML
    private ListView<RenamePreviewWrapper> previewList;

    @FXML
    private ProgressBar progressBar;

    @FXML
    public Button renameBttn;

    @FXML
    public ListView<RenamePreviewWrapper> renameList;

    private Stage mainStage;

    public void setStage(Stage s) {
        mainStage = s;
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
    void anaAction(ActionEvent event) {
        anaBttn.setDisable(true);
        loadFilesBttn.setDisable(true);
        renameBttn.setDisable(true);
        IdentifyTask task = new IdentifyTask(this);
        Thread identTask = new Thread(task);
        progressBar.progressProperty().bind(task.progressProperty());
        identTask.setDaemon(true);
        identTask.start();
    }

    @FXML
    void loadFilesAction(ActionEvent event) {
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

    @FXML
    void renameAction(ActionEvent event) {
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

    @FXML
    public void initialize() {
        previewList.setCellFactory(selectedCandidateListView -> new ListFactoryItem(false));
        renameList.setCellFactory(renamePreviewWrapperListView -> new ListFactoryItem(true));
        ObservableList<RenamePreviewWrapper> rpList = FXCollections.observableList(new ArrayList<>());
        renameList.setItems(rpList);
        previewList.setItems(rpList);
        renameList.setFixedCellSize(25);
    }

}
