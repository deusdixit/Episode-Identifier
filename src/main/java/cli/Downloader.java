package cli;

import id.gasper.opensubtitles.Opensubtitles;
import id.gasper.opensubtitles.models.authentication.LoginResult;
import id.gasper.opensubtitles.models.download.DownloadLinkResult;
import id.gasper.opensubtitles.models.subtitles.SubtitlesQuery;
import id.gasper.opensubtitles.models.subtitles.SubtitlesResult;
import picocli.CommandLine;

import java.nio.file.Paths;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "downloader", description = "download subtitles")
public class Downloader implements Callable<Integer> {

    @CommandLine.Option(names = "-u", description = "Opensubtitles.com Username")
    String username;

    @CommandLine.Option(names = "-p", description = "Opensubtitles.com Username")
    String password;

    @CommandLine.Option(names = "-t", description = "Opensubtitles.com Api-Key")
    String token;

    @CommandLine.Option(names = "--tvshow", description = "Name of the TVShow")
    String tvshow;

    @CommandLine.Option(names = "--episode", defaultValue = "-1", description = "Episode number")
    int episode;

    @CommandLine.Option(names = "--season", defaultValue = "-1", description = "Season number")
    int season;

    @CommandLine.Option(names = "--num", defaultValue = "1", description = "Number of subtitles per Episode")
    int num;

    @CommandLine.Option(names = "--imdb", defaultValue = "-1", description = "Number of subtitles per Episode")
    int imdb;

    @Override
    public Integer call() throws Exception {
        Opensubtitles os = new Opensubtitles(username, password, token);
        LoginResult lr = os.login();
        System.out.println();
        SubtitlesQuery sq = new SubtitlesQuery();
        sq.setQuery(tvshow);
        if (imdb >= 0) {
            sq.setImdbId(imdb);
        }
        if (season >= 0) {
            sq.setSeasonNumber(season);
        }
        for (int e = 1; e < 50; e++) {
            sq.setEpisodeNumber(e);
            SubtitlesResult sr = os.getSubtitles(sq.build());
            if (sr != null && sr.data.length > 0) {
                for (int i = 0; i < Math.min(num, sr.data.length); i++) {
                    String fid = sr.data[i].attributes.feature_details.feature_id + "";
                    for (int j = 0; j < Math.min(sr.data[i].attributes.files.length, num); j++) {
                        String fileid = sr.data[i].attributes.files[j].file_id + "";
                        String imdb = sr.data[i].attributes.feature_details.imdb_id + "";
                        DownloadLinkResult dlr = os.getDownloadLink(sr.data[i].attributes.files[j]);
                        os.download(dlr, Paths.get("./subs/" + fid + "/" + fid + "-" + imdb + "-" + fileid + ".srt"));
                        System.out.println("Downloading " + fileid + " Message : " + dlr.message);
                    }
                }
            } else {
                break;
            }
        }
        return null;
    }
}
