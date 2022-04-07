package gui.tasks;

import gui.controller.OpensubtitlesTabController;
import gui.models.SeasonListViewItem;
import id.gasper.opensubtitles.Opensubtitles;
import id.gasper.opensubtitles.models.features.FeatureQuery;
import id.gasper.opensubtitles.models.features.TvShow;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;

import java.util.Arrays;


public class SeasonSearchTask extends Task<Void> {

    private final Opensubtitles os;
    private final String query;
    private final int season;
    private final ListView lView;
    private final TableView osTable;
    private final OpensubtitlesTabController main;

    public SeasonSearchTask(String query, int season, OpensubtitlesTabController main, Opensubtitles os) {
        this.os = os;
        this.main = main;
        this.query = query;
        this.season = season;
        this.lView = main.seasonList;
        this.osTable = main.osTable;
    }

    @Override
    protected Void call() throws Exception {
        FeatureQuery fq = new FeatureQuery().setQuery(query).setType(FeatureQuery.Type.TVSHOW);
        lView.getItems().clear();
        TvShow[] tvshows = Arrays.stream(os.getFeatures(fq.build())).map(i -> (TvShow) i).toArray(TvShow[]::new);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < tvshows.length; i++) {
                    for (int j = 0; j < tvshows[i].attributes.seasons.length; j++) {
                        SeasonListViewItem item = new SeasonListViewItem(tvshows[i], j, main);
                        lView.getItems().add(item);
                    }
                }
            }
        });
        return null;
    }
}
