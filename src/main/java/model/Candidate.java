package model;

import hamming.Similarity;
import id.gasper.opensubtitles.Opensubtitles;
import id.gasper.opensubtitles.models.features.Episode;
import id.gasper.opensubtitles.models.features.Feature;
import id.gasper.opensubtitles.models.features.FeatureQuery;
import io.DataSet;
import io.Extract;
import subtitles.Subtitle;
import subtitles.sup.Sup;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Candidate {

    private final File MovieFile;
    private File[] SubtitleFiles = null;
    private Subtitle[] Subtitles = null;
    private List<Similarity.SimResult> Candidates = null;

    public Candidate(File f) {
        MovieFile = f;
    }

    public File[] getSubtitleFiles() throws IOException {
        if (SubtitleFiles == null) {
            SubtitleFiles = Extract.extractAll(MovieFile.toPath());
        }
        return SubtitleFiles;
    }

    public String getFilename() {
        return MovieFile.getName();
    }

    public String getAbsolutePath() {
        return MovieFile.getAbsolutePath();
    }

    public List<Similarity.SimResult> getCandidates(DataSet ds) throws IOException {
        if (Candidates == null) {
            Subtitle[] subs = getSubtitles();
            Similarity sim = new Similarity(ds);
            LinkedList<Similarity.SimResult> result = new LinkedList<>();
            for (int i = 0; i < subs.length; i++) {
                result.addAll(sim.getNeighbors(subs[i].getTimeMask(), 2));
            }
            Collections.sort(result, (a, b) -> -Double.compare(a.getAccuarcy(), b.getAccuarcy()));
            Candidates = result;
        }
        return Candidates;
    }

    public Subtitle[] getSubtitles() throws IOException {
        if (Subtitles == null) {
            File[] subFiles = getSubtitleFiles();
            Subtitles = new Subtitle[subFiles.length];
            for (int i = 0; i < Subtitles.length; i++) {
                Subtitles[i] = new Sup(subFiles[i].toPath());
            }
        }
        return Subtitles;
    }

    public String getSuggestion(DataSet ds, Opensubtitles os) throws IOException, InterruptedException {
        List<Similarity.SimResult> sim = getCandidates(ds);
        int imdbid = sim.get(0).getImdb();
        String filename = MovieFile.getName();
        String[] split = filename.split("\\.");
        String newName = "";
        if (os != null) {
            FeatureQuery fq = new FeatureQuery().setImdbId(imdbid);
            Feature[] features = os.getFeatures(fq.build());
            if (features[0] instanceof Episode) {
                Episode e = (Episode) features[0];
                newName = String.format("%s-S%02dE%02d", e.attributes.parent_title, e.attributes.season_number, e.attributes.episode_number);
            }
        }

        String result = "";
        for (int i = 0; i < split.length - 1; i++) {
            result += split[i] + ".";
        }
        result = result.substring(0, result.length() - 1);
        if (newName.length() < 1) {
            return MovieFile.getParent() + "/" + result + "{imdb-tt" + String.format("%07d}.", imdbid) + split[split.length - 1];
        } else {
            return MovieFile.getParent() + "/" + newName + "{imdb-tt" + String.format("%07d}.", imdbid) + split[split.length - 1];
        }
    }
}
