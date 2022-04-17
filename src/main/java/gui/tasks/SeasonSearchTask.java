package gui.tasks;

import gui.controller.OpensubtitlesTabController;
import gui.models.TvShowTreeItem;
import id.gasper.opensubtitles.Opensubtitles;
import id.gasper.opensubtitles.models.features.FeatureQuery;
import id.gasper.opensubtitles.models.features.TvShow;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.Arrays;


public class SeasonSearchTask extends Task<Void> {

    private final Opensubtitles os;
    private final String query;
    private final OpensubtitlesTabController main;

    public SeasonSearchTask(String query, OpensubtitlesTabController main, Opensubtitles os) {
        this.os = os;
        this.main = main;
        this.query = query;
    }

    @Override
    protected Void call() throws Exception {
        main.searchBttn.setDisable(true);
        FeatureQuery fq = new FeatureQuery().setQuery(query).setType(FeatureQuery.Type.TVSHOW);
        TvShow[] tvshows = Arrays.stream(os.getFeatures(fq.build())).map(i -> (TvShow) i).toArray(TvShow[]::new);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < tvshows.length; i++) {
                    main.ttRoot.getChildren().add(new TvShowTreeItem(tvshows[i]));
                }

            }
        });
        main.searchBttn.setDisable(false);
        return null;
    }
}
