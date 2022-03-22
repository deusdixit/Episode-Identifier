package gui.controller;

import gui.components.ImageButton;
import gui.models.ListFactoryItem;
import gui.models.RenameItem;
import gui.models.RenamePreviewWrapper;
import gui.tasks.IdentifyTask;
import io.DataSet;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.Database;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IdentifyTabController {

    @FXML
    public ImageButton anaBttn;

    @FXML
    public ImageButton loadFilesBttn;

    @FXML
    private ListView<RenamePreviewWrapper> previewList;

    @FXML
    private ProgressBar progressBar;

    @FXML
    public ImageButton renameBttn;

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

    private void addFiles(List<File> files) {
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
    }

    @FXML
    void loadFilesAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Video Files", "*.mkv", "*.mp4", "*.avi", "*.MKV", "*.MP4", "*.AVI"), new FileChooser.ExtensionFilter("All Files", "*.*"));
        List<File> files = fileChooser.showOpenMultipleDialog(mainStage);
        addFiles(files);

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
        renameList.getItems().addListener((ListChangeListener<RenamePreviewWrapper>) change -> {
            anaBttn.setDisable(renameList.getItems().size() <= 0);
            boolean isDone = previewList.getItems().size() > 0;
            for (RenamePreviewWrapper rpW : previewList.getItems()) {
                if (!rpW.isSet) {
                    isDone = false;
                    break;
                }
            }
            renameBttn.setDisable(!isDone);
        });
        renameList.setOnDragOver(event -> {
            if (event.getGestureSource() != renameList
                    && event.getDragboard().hasFiles()) {
                /* allow for both copying and moving, whatever user chooses */
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });
        renameList.setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            if (db.hasFiles()) {
                addFiles(db.getFiles());
            }
            dragEvent.setDropCompleted(true);
            dragEvent.consume();
        });
        loadFilesBttn.setImage(new Image(getClass().getResourceAsStream("/cross.png")), 50, 50);
        anaBttn.setImage(new Image(getClass().getResourceAsStream("/ana.png")), 50, 50);
        renameBttn.setImage(new Image(getClass().getResourceAsStream("/rename.png")), 50, 50);
    }

}
