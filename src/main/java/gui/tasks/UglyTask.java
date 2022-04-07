package gui.tasks;

import gui.controller.OpensubtitlesTabController;
import gui.models.TableFeature;
import id.gasper.opensubtitles.Opensubtitles;
import id.gasper.opensubtitles.models.features.Subtitle;
import id.gasper.opensubtitles.models.subtitles.SubtitlesQuery;
import id.gasper.opensubtitles.models.subtitles.SubtitlesResult;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.HashSet;

public class UglyTask extends Task<Void> {

    private final OpensubtitlesTabController main;
    private final Opensubtitles os;

    public UglyTask(OpensubtitlesTabController main, Opensubtitles os) {
        this.main = main;
        this.os = os;
    }

    @Override
    protected Void call() throws Exception {
        main.searchBttn.setDisable(true);

        SubtitlesQuery sq = new SubtitlesQuery();
        if (main.searchField.getText().length() > 0) {
            sq.setQuery(main.searchField.getText());
        }
        sq.setSeasonNumber(main.seasonSpinner.getValue());
        if (main.imdbField.getText().length() > 0) {
            sq.setParentImdbId(Integer.parseInt(main.imdbField.getText()));
        }
        HashSet<Integer> tabu = new HashSet<>();
        for (int i = 1; i < 100; i++) {
            sq.setEpisodeNumber(i);
            SubtitlesResult sr = os.getSubtitles(sq.build());
            if (sr.data.length <= 0) {
                break;
            } else {
                for (int j = 0; j < sr.data.length; j++) {
                    if (!tabu.contains(sr.data[j].attributes.feature_details.feature_id)) {
                        tabu.add(sr.data[j].attributes.feature_details.feature_id);
                        Subtitle.FeatureDetails fd = sr.data[j].attributes.feature_details;
                        TableFeature tf = new TableFeature(fd.parent_title, String.valueOf(fd.year), fd.season_number, fd.episode_number, fd.imdb_id, String.valueOf(fd.parent_feature_id));
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                main.osTable.getItems().add(tf);
                            }
                        });

                    }
                }
            }
        }
        main.searchBttn.setDisable(false);
        return null;
    }
}
