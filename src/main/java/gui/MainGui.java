package gui;

import gui.controller.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import utils.Database;

import java.io.IOException;

public class MainGui extends Application {


    private MainController controller = null;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/layout/MainWindow.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        controller.setStage(stage);
        Scene scene = new Scene(root, 700, 800);
        scene.getStylesheets().add(getClass().getResource("/css/TextfieldError.css").toExternalForm());
        stage.setMinHeight(600);
        stage.setMinWidth(500);
        stage.setScene(scene);
        stage.show();
    }


    public void show() {
        launch();
    }

    public void stop() {
        if (controller != null) {
            try {
                Database.saveDatabase(controller.getDB());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
