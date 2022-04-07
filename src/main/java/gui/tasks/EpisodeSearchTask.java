package gui.tasks;

import gui.controller.OpensubtitlesTabController;
import gui.models.TableFeature;
import id.gasper.opensubtitles.Opensubtitles;
import id.gasper.opensubtitles.models.features.Episode;
import id.gasper.opensubtitles.models.features.Feature;
import id.gasper.opensubtitles.models.features.FeatureQuery;
import id.gasper.opensubtitles.models.features.TvShow;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.LinkedList;

public class EpisodeSearchTask extends Task<Void> {

    private final TvShow.Season season;
    private final Opensubtitles os;
    private final TableView osTable;
    private final String parentId;
    private final Button downloadBttn;

    public EpisodeSearchTask(TvShow.Season season, OpensubtitlesTabController main, String pId, Opensubtitles os) {
        this.season = season;
        this.os = os;
        this.osTable = main.osTable;
        this.parentId = pId;
        this.downloadBttn = main.downloadBttn;
    }

    @Override
    protected Void call() throws Exception {
        getEpisodes(season);
        return null;
    }

    private void getEpisodes(TvShow.Season s) throws IOException, InterruptedException {
        int counter = 0;
        for (TvShow.Episode e : s.episodes) {
            updateProgress(++counter, s.episodes.length);
            FeatureQuery fq = new FeatureQuery().setFeatureId(e.feature_id);
            Feature[] fs = os.getFeatures(fq);
            LinkedList<TableFeature> sub = new LinkedList<>();
            for (Feature f : fs) {
                System.out.println(((Episode) f).attributes.title);
                sub.add(new TableFeature(f, parentId));
            }
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    osTable.getItems().addAll(sub);
                    if (osTable.getItems().size() > 0) {
                        downloadBttn.setDisable(false);
                    } else {
                        downloadBttn.setDisable(true);
                    }
                }
            });
        }
        updateProgress(0, 0);
    }
}
