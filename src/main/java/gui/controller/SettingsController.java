package gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import utils.Settings;

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
}
