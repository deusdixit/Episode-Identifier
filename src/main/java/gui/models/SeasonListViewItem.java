package gui.models;

import gui.controller.OpensubtitlesTabController;
import gui.exceptions.NoOpensubtitlesException;
import gui.tasks.EpisodeSearchTask;
import id.gasper.opensubtitles.models.features.TvShow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import utils.OsApi;


public class SeasonListViewItem extends CheckBox {

    public final TvShow.Season season;
    private final String id;

    public SeasonListViewItem(TvShow tvshow, int index, OpensubtitlesTabController main) {
        super();
        this.id = tvshow.attributes.feature_id + "-" + index;
        this.season = tvshow.attributes.seasons[index];
        this.setText(tvshow.attributes.title + " S" + tvshow.attributes.seasons[index].season_number);
        this.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean old_val, Boolean new_val) {
                if (new_val) {
                    try {
                        EpisodeSearchTask task = new EpisodeSearchTask(season, main, id, OsApi.getInstance());
                        Thread getEpisodesThread = new Thread(task);
                        main.progressBar2.progressProperty().bind(task.progressProperty());
                        getEpisodesThread.setDaemon(true);
                        getEpisodesThread.start();
                    } catch (NoOpensubtitlesException noe) {
                        noe.getMainController().showOpensubtitles();
                    }
                } else {
                    main.osTable.getItems().removeIf(x -> ((TableFeature) x).getParentId().equals(id));
                    if (main.osTable.getItems().size() == 0) {
                        main.downloadBttn.setDisable(true);
                    }
                }
            }
        });
    }
}
