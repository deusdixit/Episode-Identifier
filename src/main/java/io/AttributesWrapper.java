package io;

import id.gasper.opensubtitles.models.features.Episode;
import id.gasper.opensubtitles.models.features.Subtitle;

import java.io.Serializable;

public class AttributesWrapper implements Serializable {

    private int seasonNumber;
    private int episodeNumber;
    private String parentTitle;
    private int year;
    private String title;
    private int tmbdId;

    private int imdb;

    private static final long serialVersionUID = 4480600303123781401L;

    public AttributesWrapper(Subtitle.FeatureDetails fd) {
        seasonNumber = fd.season_number;
        episodeNumber = fd.episode_number;
        parentTitle = fd.parent_title;
        year = fd.year;
        title = fd.title;
        tmbdId = fd.tmdb_id;
        this.imdb = fd.imdb_id;
    }

    public AttributesWrapper(Episode.Attributes fd) {
        seasonNumber = fd.season_number;
        episodeNumber = fd.episode_number;
        parentTitle = fd.parent_title;
        year = Integer.parseInt(fd.year);
        title = fd.title;
        tmbdId = fd.tmdb_id;
        imdb = fd.imdb_id;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public String getParentTitle() {
        return parentTitle;
    }

    public int getYear() {
        return year;
    }

    public String getTitle() {
        return title;
    }

    public int getTmbdId() {
        return tmbdId;
    }

    public int getImdb() {
        return imdb;
    }
}
