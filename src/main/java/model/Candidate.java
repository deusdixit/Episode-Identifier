package model;

import hamming.Similarity;
import io.AttributesWrapper;
import io.DataSet;
import io.Extract;
import io.SubtitleFile;
import subtitles.Subtitle;
import subtitles.sup.Sup;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Candidate {

    private final File MovieFile;
    private SubtitleFile[] SubtitleFiles = null;
    private Subtitle[] Subtitles = null;
    private List<Similarity.SimResult> Candidates = null;
    private final DataSet ds;

    public Candidate(File f, DataSet ds) {
        MovieFile = f;
        this.ds = ds;
    }

    public SubtitleFile[] getSubtitleFiles() throws IOException {
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

    public List<Similarity.SimResult> getCandidates() throws IOException {
        if (Candidates == null) {
            Subtitle[] subs = getSubtitles();
            Similarity sim = new Similarity(ds);
            LinkedList<Similarity.SimResult> result = new LinkedList<>();
            for (int i = 0; i < subs.length; i++) {
                result.addAll(sim.getNeighbors(subs[i].getTimeMask(), 4));
            }
            Collections.sort(result, (a, b) -> -Double.compare(a.getAccuarcy(), b.getAccuarcy()));
            LinkedList<Similarity.SimResult> finalResult = new LinkedList<>();
            HashSet<Integer> container = new HashSet<>();
            for (Similarity.SimResult s : result) {
                if (!container.contains(s.getImdb())) {
                    finalResult.add(s);
                    container.add(s.getImdb());
                }
            }
            Candidates = finalResult;
        }
        return Candidates;
    }

    public Subtitle[] getSubtitles() throws IOException {
        if (Subtitles == null) {
            SubtitleFile[] subFiles = getSubtitleFiles();
            Subtitles = new Subtitle[subFiles.length];
            for (int i = 0; i < Subtitles.length; i++) {
                Subtitles[i] = new Sup(subFiles[i]);
            }
        }
        return Subtitles;
    }

    public AttributesWrapper getAttributeWrapper(Similarity.SimResult sim) {
        return ds.getAttributesByImdb(sim.getImdb());
    }

/*    public String getFilename(Similarity.SimResult sim, String template) throws IOException, InterruptedException {
        Opensubtitles os;
        try {
            os = OsApi.isLoggedIn() ? OsApi.getInstance() : null;
        } catch (NoOpensubtitlesException noe) {
            os = null;
        }
        DataSet ds = Database.getDatabase();
        int imdbid = sim.getImdb();
        String filename = MovieFile.getName();
        String[] split = filename.split("\\.");
        String newName = "";
        AttributesWrapper aWrapper;

        if (ds != null && (aWrapper = ds.getAttributesByImdb(imdbid)) != null) {
            //newName = String.format("%s-S%02dE%02d", aWrapper.getParentTitle(), aWrapper.getSeasonNumber(), aWrapper.getEpisodeNumber());
            newName = Naming.getInstance().getName(aWrapper);
        } else if (os != null) {
            FeatureQuery fq = new FeatureQuery().setImdbId(imdbid);
            Feature[] features = os.getFeatures(fq.build());
            if (features[0] instanceof Episode) {
                Episode e = (Episode) features[0];
                newName = String.format("%s-S%02dE%02d", e.attributes.parent_title, e.attributes.season_number, e.attributes.episode_number);
            }
        } else {
            for (int j = 0; j < split.length - 1; j++) {
                newName += split[j] + ".";
            }
            newName = newName.substring(0, newName.length() - 1);
        }
        return MovieFile.getParent() + "/" + newName + "." + split[split.length - 1];
    }*/

/*    public String[] getSuggestions(Opensubtitles os, int num, String template) throws IOException, InterruptedException {
        List<Similarity.SimResult> sim = getCandidates();
        String[] resultArr = new String[Math.min(num, sim.size())];
        for (int i = 0; i < resultArr.length; i++) {
            resultArr[i] = getFilename(sim.get(i), template);
        }
        return resultArr;
    }*/

/*    public String getSuggestion(Opensubtitles os, String template) throws IOException, InterruptedException {
        return getSuggestions(os, 1, template)[0];
    }*/

    public void deleteSubtitleFiles() {
        if (SubtitleFiles != null) {
            for (File f : SubtitleFiles) {
                f.delete();
            }
            SubtitleFiles = null;
        }
    }
}
