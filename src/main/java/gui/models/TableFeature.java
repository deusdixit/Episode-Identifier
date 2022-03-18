package gui.models;

import id.gasper.opensubtitles.models.features.Episode;
import id.gasper.opensubtitles.models.features.Feature;
import id.gasper.opensubtitles.models.features.Movie;
import id.gasper.opensubtitles.models.features.TvShow;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TableFeature {

    private final SimpleStringProperty title, year;
    private final SimpleIntegerProperty season, episode, imdb;
    private final String parentId;

    public TableFeature(Feature feature, String pId) {
        this.parentId = pId;
        this.title = new SimpleStringProperty();
        this.year = new SimpleStringProperty();
        this.season = new SimpleIntegerProperty();
        this.episode = new SimpleIntegerProperty();
        this.imdb = new SimpleIntegerProperty();
        if (feature instanceof Episode) {
            Episode e = (Episode) feature;
            this.title.set(e.attributes.title);
            this.year.set(e.attributes.year);
            this.season.set(e.attributes.season_number);
            this.episode.set(e.attributes.episode_number);
            this.imdb.set(e.attributes.imdb_id);
        } else if (feature instanceof TvShow) {
            TvShow t = (TvShow) feature;
            this.title.set(t.attributes.title);
            this.year.set(t.attributes.year);
            this.imdb.set(t.attributes.imdb_id);
        } else if (feature instanceof Movie) {
            Movie m = (Movie) feature;
            this.title.set(m.attributes.title);
            this.year.set(m.attributes.year);
            this.imdb.set(m.attributes.imdb_id);
        }
    }

    public String getParentId() {
        return parentId;
    }

    public SimpleStringProperty titleProperty() {
        return title;
    }

    public SimpleStringProperty yearProperty() {
        return year;
    }

    public SimpleIntegerProperty imdbProperty() {
        return imdb;
    }

    public SimpleIntegerProperty seasonProperty() {
        return season;
    }

    public SimpleIntegerProperty episodeProperty() {
        return episode;
    }
}
