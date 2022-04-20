package gui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import utils.Settings;

import java.io.File;

public class SettingsController {


    @FXML
    private Button ffmpegSelectButton;

    @FXML
    private TextField ffmpegTextfield;

    @FXML
    private CheckBox tempFilesCheckBox;

    @FXML
    private TextField ffprobeTextfield;

    @FXML
    private Button selectFfprobeButton;


    @FXML
    public void initialize() {
        ffmpegTextfield.setText(Settings.getInstace().getFfmpegPath());
        tempFilesCheckBox.setSelected(Settings.getInstace().getKeepTemporary());
        ffprobeTextfield.setText(Settings.getInstace().getFfprobePath());
        tempFilesCheckBox.selectedProperty().addListener((obs, oldValue, newValue) -> Settings.getInstace().putKeepTemporary(newValue));
        ffmpegTextfield.textProperty().addListener((obs, oldValue, newValue) -> Settings.getInstace().putFfmpegPath(newValue));
        ffprobeTextfield.textProperty().addListener((obs, oldValue, newValue) -> Settings.getInstace().putFfprobePath(newValue));
    }

    @FXML
    void ffmpegButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FFMPEG Executable", "*", "*"), new FileChooser.ExtensionFilter("All Files", "*.*"));
        File f = fileChooser.showOpenDialog(MainController.getInstance().getStage());
        if (f != null) {
            ffmpegTextfield.setText(f.getAbsolutePath());
        }
    }

    @FXML
    void ffprobeButtonAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("FFPROBE Executable", "*", "*"), new FileChooser.ExtensionFilter("All Files", "*.*"));
        File f = fileChooser.showOpenDialog(MainController.getInstance().getStage());
        if (f != null) {
            ffprobeTextfield.setText(f.getAbsolutePath());
        }
    }
}
