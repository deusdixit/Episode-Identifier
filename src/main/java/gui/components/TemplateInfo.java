package gui.components;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.GridPane;

import java.io.IOException;

public class TemplateInfo extends GridPane {

    public TemplateInfo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/layout/TemplateInfo.fxml"));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException iex) {
            iex.printStackTrace();
        }
    }
}
