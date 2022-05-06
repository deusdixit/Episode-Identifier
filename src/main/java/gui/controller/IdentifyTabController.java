package gui.controller;

import cli.Runner;
import gui.components.ImageButton;
import gui.components.TemplateInfo;
import gui.models.ListFactoryItem;
import gui.models.RenameItem;
import gui.models.RenamePreviewWrapper;
import gui.tasks.IdentifyTask;
import io.Extract;
import io.SubtitleFile;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import subtitles.sup.Sup;
import utils.Drawing;
import utils.Naming;
import utils.Settings;

import java.io.File;
import java.io.IOException;
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

    private final SimpleBooleanProperty taskRunning = new SimpleBooleanProperty(false);
    private static final Logger log = LoggerFactory.getLogger(IdentifyTabController.class);

    public void setStage(Stage s) {
        mainStage = s;
    }

    @FXML
    void anaAction() {
        boolean ffp = Settings.getInstace().isFfprobeValid();
        boolean ffm = Settings.getInstace().isFfmpegValid();
        if (ffm && ffp) {
            anaBttn.setDisable(true);
            loadFilesBttn.setDisable(true);
            IdentifyTask task = new IdentifyTask(this);
            taskRunning.set(true);
            task.setOnSucceeded((e) -> {
                sortList();
                taskRunning.set(false);
            });
            Thread identTask = new Thread(task);
            progressBar.progressProperty().bind(task.progressProperty());

            identTask.setDaemon(true);
            identTask.start();
        } else {
            Alert alert;
            if (!ffm && !ffp) {
                alert = new Alert(Alert.AlertType.ERROR, "FFMPEG and FFPROBE error. Check File -> Settings", ButtonType.OK);
            } else if (!ffm) {
                alert = new Alert(Alert.AlertType.ERROR, "FFMPEG error. Check File -> Settings", ButtonType.OK);
            } else {
                alert = new Alert(Alert.AlertType.ERROR, "FFPROBE error. Check File -> Settings", ButtonType.OK);
            }
            alert.show();
        }
    }
    public void sortList() {
        previewList.getItems().sort((a, b) -> {
            String filenameA = a.getPreviewItem().getSelectedFilename();
            String filenameB = b.getPreviewItem().getSelectedFilename();
            if (a.getPreviewItem().getValue().getComboBox() == null && b.getPreviewItem().getValue().getComboBox() != null) {
                return -1;
            } else if (a.getPreviewItem().getValue().getComboBox() != null && b.getPreviewItem().getValue().getComboBox() == null) {
                return 1;
            } else if (a.getPreviewItem().getValue().getComboBox() == null && b.getPreviewItem().getValue().getComboBox() == null) {
                return 0;
            } else {
                if (a.getPreviewItem().getSelectedAccuracy() >= 0.2 && b.getPreviewItem().getSelectedAccuracy() < 0.2) {
                    return 1;
                } else if (a.getPreviewItem().getSelectedAccuracy() < 0.2 && b.getPreviewItem().getSelectedAccuracy() >= 0.2) {
                    return -1;
                } else if (a.getPreviewItem().getSelectedAccuracy() >= 0.2 && b.getPreviewItem().getSelectedAccuracy() >= 0.2) {
                    return filenameA.compareTo(filenameB);
                } else {
                    return 0;
                }
            }
        });
        renameList.refresh();
        previewList.refresh();
    }
    private void addFiles(List<File> files) {
        if (files != null && files.size() > 0) {
            for (File f : files) {
                RenamePreviewWrapper rpW = new RenamePreviewWrapper(new RenameItem(f));
                rpW.addListener(observable -> {
                    renameList.refresh();
                    previewList.refresh();
                });
                if (!renameList.getItems().contains(rpW)) {
                    renameList.getItems().add(rpW);
                }
            }
        }
    }

    @FXML
    public void infoBttnAction() {
        TemplateInfo ti = new TemplateInfo();
        Stage stage = new Stage();
        stage.setScene(new Scene(ti));
        stage.show();
    }

    @FXML
    void loadFilesAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Video Files", "*.mkv", "*.mp4", "*.avi", "*.MKV", "*.MP4", "*.AVI"), new FileChooser.ExtensionFilter("All Files", "*.*"));
        List<File> files = fileChooser.showOpenMultipleDialog(mainStage);
        addFiles(files);
    }

    @FXML
    void renameAction() {
        if (renameList.getItems().size() != previewList.getItems().size()) {
            log.error("RenameList doesn't match PreviewList");
        } else if (isRenameListValid(renameList.getItems())) {
            // Rename all files to distinct temporary file names to prevent data loss
            File[] tmpFiles = new File[renameList.getItems().size()];
            int tmpId = ThreadLocalRandom.current().nextInt();
            for (int i = 0; i < renameList.getItems().size(); i++) {
                if (renameList.getItems().get(i).isActive().get()) {
                    String tmpName = i + "-" + tmpId;
                    tmpFiles[i] = new File(renameList.getItems().get(i).getRenameItem().getValue().getParent() + File.separator + tmpName);
                    boolean success = renameList.getItems().get(i).getRenameItem().getValue().renameTo(tmpFiles[i]);
                    if (!success) {
                        log.error("Couldn't rename file " + renameList.getItems().get(i).getRenameItem().getValue().toString() + " to " + tmpFiles[i].toString());
                    }
                }
            }
            // Rename the files with temporary filenames to the suggested filenames
            for (int i = 0; i < renameList.getItems().size(); i++) {
                if (renameList.getItems().get(i).isActive().get()) {
                    File oldFile = tmpFiles[i];
                    int lIndex = renameList.getItems().get(i).getRenameItem().getValue().toString().lastIndexOf(".");
                    String extension = renameList.getItems().get(i).getRenameItem().getValue().toString().substring(lIndex + 1);
                    File newFile = new File(renameList.getItems().get(i).getRenameItem().getValue().getParent() + File.separator + previewList.getItems().get(i).getPreviewItem().getSelectedFilename() + "." + extension);
                    if (oldFile.renameTo(newFile)) {
                        log.debug("Renamed " + oldFile + " to " + newFile);
                    } else {
                        log.error("Renaming file " + oldFile + " to " + newFile + " failed");
                    }
                }
            }
            renameList.getItems().clear();
            previewList.getItems().clear();
        } else {
            log.error("One or more files share the same suggested rename filename");
            Alert alert = new Alert(Alert.AlertType.ERROR, "One or more files share the same suggested rename filename", ButtonType.OK);
            alert.show();
        }
    }
    @FXML
    public void templateTextChanged() {
        Settings.getInstace().putTemplate(templateTextfield.getText());
        Naming.getInstance().fireOnChange();
        previewList.refresh();
    }

    public boolean isRenameListValid(ObservableList<RenamePreviewWrapper> list) {
        HashSet<String> container = new HashSet<>();
        for (RenamePreviewWrapper rpw : list) {
            if (rpw.getPreviewItem().isActive().get()) {
                if (container.contains(rpw.getPreviewItem().getSelectedFilename())) {
                    return false;
                } else {
                    container.add(rpw.getPreviewItem().getSelectedFilename());
                }
            }
        }
        return container.size() > 0;
    }
    @FXML
    public void initialize() {
        previewList.setCellFactory(selectedCandidateListView -> new ListFactoryItem(false));
        renameList.setCellFactory(renamePreviewWrapperListView -> new ListFactoryItem(true));
        ObservableList<RenamePreviewWrapper> rpList = FXCollections.observableList(new ArrayList<>(), renamePreviewWrapper -> new Observable[]{renamePreviewWrapper.isActive()});
        renameList.setItems(rpList);
        previewList.setItems(rpList);
        renameList.setFixedCellSize(25);
        renameList.getItems().addListener((ListChangeListener<RenamePreviewWrapper>) change -> {
            anaBttn.setDisable(renameList.getItems().size() <= 0);
            Set<Node> nodes = renameList.lookupAll(".scroll-bar");
            Set<Node> nodes2 = previewList.lookupAll(".scroll-bar");
            LinkedList<ScrollBar> vertical = new LinkedList<>();
            for (Node n : nodes) {
                if (n instanceof ScrollBar) {
                    ScrollBar s = (ScrollBar) n;
                    if (s.getOrientation() == Orientation.VERTICAL) {
                        vertical.add(s);
                    }
                }
            }
            for (Node n : nodes2) {
                if (n instanceof ScrollBar) {
                    ScrollBar s = (ScrollBar) n;
                    if (s.getOrientation() == Orientation.VERTICAL) {
                        for (ScrollBar x : vertical) {
                            x.valueProperty().bindBidirectional(s.valueProperty());
                        }
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
        loadFilesBttn.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/cross.png"))), 50, 50);
        anaBttn.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/ana.png"))), 50, 50);
        renameBttn.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/rename.png"))), 50, 50);
        infoBttn.setImage(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/info.png"))), 20, 20);
        ContextMenu cm = new ContextMenu();

        MenuItem removeItem = new MenuItem("Remove");
        if (Runner.DEBUG_MODE) {
            MenuItem mItem = new MenuItem("Export Timeline");
            cm.getItems().add(mItem);
            mItem.setOnAction((event) -> {
                if (renameList.getSelectionModel().getSelectedItems().size() > 0) {
                    RenamePreviewWrapper rpw = renameList.getSelectionModel().getSelectedItem();
                    try {
                        int streamid = Extract.getSubtitleIds(rpw.getRenameItem().getValue().toPath())[0].streamID;
                        String tmpfile = System.getProperty("java.io.tmpdir") + File.separator + "test.sup";
                        Extract.extract(rpw.getRenameItem().getValue().toPath(), streamid, new File(tmpfile));
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.setTitle("Save timeline");
                        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"));
                        File file = fileChooser.showSaveDialog(mainStage);
                        Sup tsub = new Sup(new SubtitleFile(tmpfile, "", ""));
                        Drawing.draw(tsub.getTimeMask(), file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
        cm.getItems().add(removeItem);
        removeItem.setOnAction((event) -> {
            if (renameList.getSelectionModel().getSelectedItems().size() > 0) {
                renameList.getItems().remove(renameList.getSelectionModel().getSelectedItem());
            }
        });
        renameList.setContextMenu(cm);

        templateTextfield.setText(Settings.getInstace().getTemplate());
        renameBttn.disableProperty().bind(Bindings.createBooleanBinding(() -> !isRenameListValid(previewList.getItems()), previewList.getItems()).or(BooleanBinding.booleanExpression(taskRunning)));
    }

}
