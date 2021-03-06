package gui.models;

import id.gasper.opensubtitles.models.features.TvShow;
import javafx.scene.control.TreeItem;

import java.util.Arrays;
import java.util.Comparator;

public class TvShowTreeItem extends TreeItem<TreeItemWrapper> {

    public TvShowTreeItem(TvShow t) {
        super(new TreeItemWrapper(t));
        for (int i = 0; i < t.attributes.seasons.length; i++) {
            TvShow.Episode[] episodes = t.attributes.seasons[i].episodes;
            TreeItem<TreeItemWrapper> season = new TreeItem<>();
            TreeItemWrapper tw = new TreeItemWrapper(t.attributes.seasons[i]);
            tw.setImgUrl(t.attributes.img_url);
            season.setValue(tw);
            Arrays.sort(episodes, Comparator.comparingInt(x -> x.episode_number));
            //HashSet<Integer> container = new HashSet<>();
            for (TvShow.Episode value : episodes) {
                TreeItem<TreeItemWrapper> episode = new TreeItem<>();
                TreeItemWrapper tw2 = new TreeItemWrapper(value);
                tw2.setImgUrl(t.attributes.img_url);
                episode.setValue(tw2);
                season.getChildren().add(episode);
                //container.add(episodes[j].episode_number);
            }
            this.getChildren().add(season);
        }
    }

}
