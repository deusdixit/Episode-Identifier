package gui.tasks;

import gui.controller.OpensubtitlesTabController;
import gui.models.TableFeature;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import utils.Database;
import utils.OsApi;

import java.io.IOException;

public class DownloadTask extends Task<Void> {

    private final OpensubtitlesTabController main;

    public DownloadTask(OpensubtitlesTabController main) {
        this.main = main;
    }

    @Override
    protected Void call() throws Exception {
        ObservableList<TableFeature> list = main.osTable.getItems();
        if (!list.isEmpty()) {
            int counter = 0;
            for (TableFeature tf : list) {
                try {
                    Database.downloadAutomatic(OsApi.getInstance(), tf.imdbProperty().getValue());
                    updateProgress(++counter, list.size());
                } catch (IOException ioe) {

                } catch (ClassNotFoundException cnfe) {

                } catch (InterruptedException ie) {

                }
            }
            updateProgress(0, 0);
        }
        main.downloadBttn.setDisable(false);
        main.searchBttn.setDisable(false);
        return null;
    }
}
