package utils;

import id.gasper.opensubtitles.Opensubtitles;
import id.gasper.opensubtitles.models.download.DownloadLinkResult;
import id.gasper.opensubtitles.models.features.Subtitle;
import id.gasper.opensubtitles.models.subtitles.SubtitlesQuery;
import id.gasper.opensubtitles.models.subtitles.SubtitlesResult;
import io.DataSet;
import io.Dataloader;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Database {

    private static final String DEFAULT_DB_PATH = "./database.ser";
    private static final String DEFAULT_SUB_PATH = "./subs/";
    private static DataSet ds = null;

    public static DataSet getDatabase() throws IOException, ClassNotFoundException {
        if (ds != null) {
            return ds;
        }
        Path path = Paths.get(DEFAULT_DB_PATH);
        Path subdir = Paths.get(DEFAULT_SUB_PATH);
        if (path.toFile().exists()) {
            ds = Dataloader.load(path);
            ;
        } else {
            if (!subdir.toFile().exists()) {
                subdir.toFile().mkdirs();
            }
            ds = Dataloader.loadSrt(Paths.get(DEFAULT_SUB_PATH), true);
        }
        return ds;
    }

    public static void loadSubtitles() throws IOException, ClassNotFoundException {
        loadSubtitles(Paths.get(DEFAULT_SUB_PATH));
    }

    public static void loadSubtitles(Path path) throws IOException, ClassNotFoundException {
        Dataloader.loadSrt(path, true, getDatabase());
    }

    public static void downloadAutomatic(Opensubtitles os, int imdb) throws IOException, InterruptedException, ClassNotFoundException {
        SubtitlesQuery sq = new SubtitlesQuery().setImdbId(imdb);
        SubtitlesResult sr = os.getSubtitles(sq.build());
        DataSet ds = getDatabase();
        for (int i = 0; i < sr.data.length; i++) {
            String fid = sr.data[i].attributes.feature_details.feature_id + "";
            for (int j = 0; j < sr.data[i].attributes.files.length; j++) {
                Subtitle.FileObject fd = sr.data[i].attributes.files[j];
                if (!ds.contains(imdb, fd.file_id)) {
                    DownloadLinkResult dlr = os.getDownloadLink(fd);
                    os.download(dlr, Paths.get("./subs/" + fid + "/" + fid + "-" + imdb + "-" + fd.file_id + ".srt"));
                    System.out.println("Downloading " + fd.file_id + " Message : " + dlr.message);
                    return;
                }
            }
        }
    }
}