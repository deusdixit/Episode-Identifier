package gui.models;

import gui.controller.OpensubtitlesController;
import gui.tasks.EpisodeSearchTask;
import id.gasper.opensubtitles.models.features.TvShow;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;


public class SeasonListViewItem extends CheckBox {

    public final TvShow.Season season;
    private final String id;

    public SeasonListViewItem(TvShow tvshow, int index, OpensubtitlesController main) {
        super();
        this.id = tvshow.attributes.feature_id + "-" + index;
        this.season = tvshow.attributes.seasons[index];
        this.setText(tvshow.attributes.title + " S" + tvshow.attributes.seasons[index].season_number);
        this.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean old_val, Boolean new_val) {
                if (new_val) {
                    EpisodeSearchTask task = new EpisodeSearchTask(season, main, id);
                    Thread getEpisodesThread = new Thread(task);
                    getEpisodesThread.setDaemon(true);
                    getEpisodesThread.start();
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
