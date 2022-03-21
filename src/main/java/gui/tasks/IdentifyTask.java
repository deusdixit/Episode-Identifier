package gui.tasks;

import gui.controller.MainController;
import gui.models.PreviewItem;
import gui.models.RenamePreviewWrapper;
import hamming.Similarity;
import javafx.application.Platform;
import javafx.concurrent.Task;
import model.Candidate;
import utils.OsApi;

import java.util.ArrayList;
import java.util.List;

public class IdentifyTask extends Task<Void> {

    private final MainController main;

    public IdentifyTask(MainController main) {
        this.main = main;
    }

    @Override
    protected Void call() throws Exception {
        int counter = 0;
        for (RenamePreviewWrapper rpItem : this.main.renameList.getItems()) {
            Candidate can = new Candidate(rpItem.getRenameItem().getValue(), main.getDB());
            try {
                List<Similarity.SimResult> result = can.getCandidates();
                List<PreviewItem.ComboItem> combo = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {
                    combo.add(new PreviewItem.ComboItem(result.get(i), can.getFilename(result.get(i), OsApi.getInstance())));
                }
                updateProgress(counter, main.renameList.getItems().size() - 1);
                counter++;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        rpItem.setPreviewItem(combo);
                    }
                });

            } catch (Exception ex) {

            }
        }
        main.anaBttn.setDisable(false);
        main.loadFilesBttn.setDisable(false);
        main.renameBttn.setDisable(false);
        return null;
    }
}
