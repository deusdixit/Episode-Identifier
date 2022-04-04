package gui.tasks;

import gui.components.PreviewListItem;
import gui.controller.IdentifyTabController;
import gui.models.RenamePreviewWrapper;
import hamming.Similarity;
import javafx.application.Platform;
import javafx.concurrent.Task;
import model.Candidate;

import java.util.ArrayList;
import java.util.List;

public class IdentifyTask extends Task<Void> {

    private final IdentifyTabController main;

    public IdentifyTask(IdentifyTabController main) {
        this.main = main;
    }

    @Override
    protected Void call() throws Exception {
        int counter = 0;
        for (RenamePreviewWrapper rpItem : this.main.renameList.getItems()) {
            Candidate can = new Candidate(rpItem.getRenameItem().getValue(), main.getDB());
            try {
                List<Similarity.SimResult> result = can.getCandidates();
                List<PreviewListItem.ComboItem> combo = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {
                    combo.add(new PreviewListItem.ComboItem(result.get(i), can.getFilename(result.get(i), main.templateTextfield.getText())));
                }
                updateProgress(++counter, main.renameList.getItems().size());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        rpItem.setPreviewItem(combo);
                    }
                });

            } catch (Exception ex) {
                System.out.println(ex.getLocalizedMessage());
                System.out.println();
            }
        }
        updateProgress(0, 0);
        main.anaBttn.setDisable(false);
        main.loadFilesBttn.setDisable(false);
        main.renameBttn.setDisable(false);
        return null;
    }
}
