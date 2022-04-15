package gui.models;

import id.gasper.opensubtitles.models.features.TvShow;
import utils.Database;

public class TreeItemWrapper {

    public enum Type {
        TVSHOW, SEASON, EPISODE;
    }

    private String name, title, year;
    private String imdb, numDb;
    private String imgUrl;
    private int tmdb;
    public Type type;

    public TreeItemWrapper(TvShow t) {
        name = (t.attributes.title);
        title = ("...");
        imdb = t.attributes.imdb_id + "";
        numDb = "";
        year = t.attributes.year;
        imgUrl = t.attributes.img_url;
        tmdb = t.attributes.tmdb_id;
        type = Type.TVSHOW;
    }

    public TreeItemWrapper(String name, String title, int imdb, int tmdb, int year, String imgUrl) {
        this.name = name;
        this.title = title;
        this.imdb = String.valueOf(imdb);
        try {
            numDb = String.valueOf(Database.getDatabase().getByImdb(imdb).size());
        } catch (Exception ex) {
            numDb = "";
        }
        this.year = String.valueOf(year);
        this.imgUrl = imgUrl;
        this.tmdb = tmdb;
        type = Type.EPISODE;
    }

    public TreeItemWrapper(TvShow.Season s) {
        name = ("Season " + s.season_number);
        title = ("...");
        imdb = "-";
        numDb = "-";
        year = "-";
        tmdb = 0;
        type = Type.SEASON;
    }

    public TreeItemWrapper(TvShow.Episode e) {
        name = ("Episode " + e.episode_number);
        title = (e.title);
        imdb = e.feature_imdb_id + "";
        try {
            numDb = String.valueOf(Database.getDatabase().getByImdb(e.feature_imdb_id).size());
        } catch (Exception ex) {
            numDb = "";
        }
        year = "-";
        tmdb = 0;
        type = Type.EPISODE;
    }

    public String getName() {
        return name;
    }

    public String getImdb() {
        return imdb;
    }

    public String getNumDb() {
        return numDb;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getImgUrl() {
        return imgUrl != null ? imgUrl : "";
    }

    public void setImgUrl(String url) {
        imgUrl = url;
    }

    public String getTmdb() {
        return tmdb > 0 ? String.valueOf(tmdb) : "-";
    }
}
