package gui.tasks;

import gui.controller.MainController;
import javafx.application.Platform;
import javafx.concurrent.Task;
import model.Candidate;

import java.io.File;

public class IdentifyTask extends Task<Void> {

    private final MainController main;

    public IdentifyTask(MainController main) {
        this.main = main;
    }

    @Override
    protected Void call() throws Exception {
        int counter = 0;
        for (Object obj : this.main.renameList.getItems()) {
            Candidate can = new Candidate((File) obj);
            try {
                String newName = can.getSuggestion(this.main.getDB(), this.main.getOS());
                System.out.println(newName);
                updateProgress(counter, main.renameList.getItems().size() - 1);
                counter++;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        main.previewList.getItems().add(newName);
                    }
                });

            } catch (Exception ex) {

            }
        }
        main.anaBttn.setDisable(false);
        main.loadFilesBttn.setDisable(false);

        return null;
    }
}
