package model;

import io.AttributesWrapper;
import io.DataSet;
import io.Extract;
import io.SubtitleFile;
import subtitles.Subtitle;
import subtitles.sup.Sup;
import utils.Similarity;

import java.io.File;
import java.io.IOException;
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
            for (Subtitle sub : subs) {
                result.addAll(sim.getNeighbors(sub.getTimeMask(), 4));
            }
            result.sort((a, b) -> -Double.compare(a.getAccuarcy(), b.getAccuarcy()));
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

    public void deleteSubtitleFiles() {
        if (SubtitleFiles != null) {
            for (File f : SubtitleFiles) {
                f.delete();
            }
            SubtitleFiles = null;
        }
    }
}
