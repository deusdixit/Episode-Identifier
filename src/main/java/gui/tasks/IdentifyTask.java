package gui.tasks;

import gui.components.PreviewListItem;
import gui.controller.IdentifyTabController;
import gui.models.RenamePreviewWrapper;
import hamming.Similarity;
import javafx.application.Platform;
import javafx.concurrent.Task;
import model.Candidate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.Database;
import utils.Settings;

import java.util.ArrayList;
import java.util.List;

public class IdentifyTask extends Task<Void> {

    private final IdentifyTabController main;

    private static final Logger log = LoggerFactory.getLogger(IdentifyTask.class);

    public IdentifyTask(IdentifyTabController main) {
        this.main = main;
    }

    @Override
    protected Void call() throws Exception {
        int counter = 0;
        for (RenamePreviewWrapper rpItem : this.main.renameList.getItems()) {
            Candidate can = new Candidate(rpItem.getRenameItem().getValue(), Database.getDatabase());
            try {
                List<Similarity.SimResult> result = can.getCandidates();
                List<PreviewListItem.ComboItem> combo = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {
                    combo.add(new PreviewListItem.ComboItem(result.get(i), can.getAttributeWrapper(result.get(i))));
                }
                updateProgress(++counter, main.renameList.getItems().size());
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        rpItem.setPreviewItem(combo);
                    }
                });
                if (!Settings.getInstace().getKeepTemporary()) {
                    can.deleteSubtitleFiles();
                }
            } catch (Exception ex) {
                log.error(ex.toString());
            }
        }
        updateProgress(0, 0);
        main.anaBttn.setDisable(false);
        main.loadFilesBttn.setDisable(false);
        main.renameBttn.setDisable(false);
        return null;
    }
}
