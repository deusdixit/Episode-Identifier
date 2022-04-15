package gui.controller;

import gui.components.ImageButton;
import gui.models.ListFactoryItem;
import gui.models.RenameItem;
import gui.models.RenamePreviewWrapper;
import gui.tasks.IdentifyTask;
import io.DataSet;
import io.Extract;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import subtitles.sup.Sup;
import utils.Database;
import utils.Drawing;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class IdentifyTabController {

    @FXML
    public ImageButton anaBttn;

    @FXML
    public ImageButton loadFilesBttn;

    @FXML
    private ListView<RenamePreviewWrapper> previewList;

    @FXML
    public TextField templateTextfield;

    @FXML
    private ProgressBar progressBar;

    @FXML
    public ImageButton renameBttn;

    @FXML
    public ImageButton infoBttn;

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
        task.setOnSucceeded((e) -> {
            sortList();
        });
        Thread identTask = new Thread(task);
        progressBar.progressProperty().bind(task.progressProperty());
        identTask.setDaemon(true);
        identTask.start();
    }

    public void sortList() {
        Collections.sort(previewList.getItems(), new Comparator<RenamePreviewWrapper>() {
            @Override
            public int compare(RenamePreviewWrapper a, RenamePreviewWrapper b) {
                return a.getPreviewItem().getSelectedFilename().compareTo(b.getPreviewItem().getSelectedFilename());
            }
        });
        renameList.refresh();
        previewList.refresh();
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
    public void infoBttnAction() {

    }

    @FXML
    void loadFilesAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Video Files", "*.mkv", "*.mp4", "*.avi", "*.MKV", "*.MP4", "*.AVI"), new FileChooser.ExtensionFilter("All Files", "*.*"));
        List<File> files = fileChooser.showOpenMultipleDialog(mainStage);
        addFiles(files);
    }

    /*
    FIX DATA LOSS BUG WITH RENAMING
     */
    @FXML
    void renameAction(ActionEvent event) {
        if (renameList.getItems().size() != previewList.getItems().size()) {
            System.out.println("Error");
        } else {
            // Rename all files to distinct temporary file names to prevent data loss
            File[] tmpFiles = new File[renameList.getItems().size()];
            int tmpId = ThreadLocalRandom.current().nextInt();
            for (int i = 0; i < renameList.getItems().size(); i++) {
                if (renameList.getItems().get(i).isActive()) {
                    String tmpName = i + "-" + tmpId;
                    tmpFiles[i] = new File(renameList.getItems().get(i).getRenameItem().getValue().getParent() + "/" + tmpName);
                    renameList.getItems().get(i).getRenameItem().getValue().renameTo(tmpFiles[i]);
                }
            }
            // Rename the files with temporary filenames to the suggested filenames
            for (int i = 0; i < renameList.getItems().size(); i++) {
                if (renameList.getItems().get(i).isActive()) {
                    File oldFile = tmpFiles[i];
                    File newFile = new File(previewList.getItems().get(i).getPreviewItem().getSelectedFilename());
                    if (oldFile.renameTo(newFile)) {
                        System.out.println("Renamed to " + newFile);
                    } else {
                        System.out.println("Error");
                    }
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
            Set<Node> nodes = renameList.lookupAll(".scroll-bar");
            Set<Node> nodes2 = previewList.lookupAll(".scroll-bar");
            LinkedList<ScrollBar> horizontal = new LinkedList<>();
            LinkedList<ScrollBar> vertical = new LinkedList<>();
            for (Node n : nodes) {
                if (n instanceof ScrollBar) {
                    ScrollBar s = (ScrollBar) n;
                    switch (s.getOrientation()) {
                        case HORIZONTAL -> horizontal.add(s);
                        case VERTICAL -> vertical.add(s);
                    }
                }
            }
            for (Node n : nodes2) {
                if (n instanceof ScrollBar) {
                    ScrollBar s = (ScrollBar) n;
                    switch (s.getOrientation()) {
                        case HORIZONTAL:
                            for (ScrollBar x : horizontal) {
                                x.valueProperty().bindBidirectional(s.valueProperty());
                            }
                            break;
                        case VERTICAL:
                            for (ScrollBar x : vertical) {
                                x.valueProperty().bindBidirectional(s.valueProperty());
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
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
        loadFilesBttn.setImage(new Image(getClass().getResourceAsStream("/icons/cross.png")), 50, 50);
        anaBttn.setImage(new Image(getClass().getResourceAsStream("/icons/ana.png")), 50, 50);
        renameBttn.setImage(new Image(getClass().getResourceAsStream("/icons/rename.png")), 50, 50);
        infoBttn.setImage(new Image(getClass().getResourceAsStream("/icons/info.png")), 20, 20);
        ContextMenu cm = new ContextMenu();
        MenuItem mItem = new MenuItem("Export Timeline");
        cm.getItems().add(mItem);
        renameList.setContextMenu(cm);
        mItem.setOnAction((event) -> {
            if (renameList.getSelectionModel().getSelectedItems().size() > 0) {

                RenamePreviewWrapper rpw = renameList.getSelectionModel().getSelectedItem();
                try {
                    int streamid = Extract.getSubtitleIds(rpw.getRenameItem().getValue().toPath())[0].streamID;
                    Extract.extract(rpw.getRenameItem().getValue().toPath(), streamid, new File("/tmp/test.sup"));
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Save timeline");
                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"));
                    File file = fileChooser.showSaveDialog(mainStage);
                    Sup tsub = new Sup(Paths.get("/tmp/test.sup"));
                    Drawing.draw(tsub.getTimeMask(), file);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

}
