package gui.tasks;

import gui.controller.OpensubtitlesTabController;
import gui.models.TreeItemWrapper;
import javafx.concurrent.Task;
import utils.Database;

import java.io.IOException;
import java.util.List;

public class DownloadTask extends Task<Void> {

    private final OpensubtitlesTabController main;
    private final List<TreeItemWrapper> items;

    public DownloadTask(OpensubtitlesTabController main, List<TreeItemWrapper> list) {
        this.main = main;
        this.items = list;
    }

    @Override
    protected Void call() throws Exception {
        main.setDisableDownloadButtons(true);
        int counter = 0;
        for (TreeItemWrapper tiw : items) {
            if (tiw.type == TreeItemWrapper.Type.EPISODE) {
                try {
                    Database.downloadAutomatic(Integer.parseInt(tiw.getImdb()));
                    updateProgress(++counter, items.size());
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                } catch (ClassNotFoundException cnfe) {
                    cnfe.printStackTrace();
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                }
            }
        }
        updateProgress(0, 0);
        main.searchBttn.setDisable(false);
        main.setDisableDownloadButtons(false);
        return null;
    }
}
