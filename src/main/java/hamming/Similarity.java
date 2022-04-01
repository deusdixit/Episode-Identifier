package hamming;

import io.DataSet;
import io.Item;

import java.util.BitSet;
import java.util.Collections;
import java.util.LinkedList;

public class Similarity {

    private final DataSet DS;

    public Similarity(DataSet data) {
        DS = data;
    }

    private double getSimilarity(BitSet x, BitSet y) {
        BitSet copy = (BitSet) y.clone();
        BitSet copy2 = (BitSet) y.clone();
        copy.and(x);
        copy2.or(x);
        double maxLength = (double) copy2.cardinality();
        double dist = (double) copy.cardinality() / maxLength;
        return dist;
    }

    private double getSimilarityInverse(BitSet x, BitSet y) {
        BitSet copy = (BitSet) y.clone();
        BitSet copy2 = (BitSet) y.clone();
        BitSet xcopy = (BitSet) x.clone();
        copy.flip(0, copy.length());
        copy2.flip(0, copy2.length());
        xcopy.flip(0, xcopy.length());
        copy.and(xcopy);
        copy2.or(xcopy);
        double maxLength = (double) copy2.cardinality();
        double dist = (double) copy.cardinality() / maxLength;
        return dist;
    }

    public LinkedList<SimResult> getNeighbors(BitSet x, int n) {
        LinkedList<SimResult> result = new LinkedList<>();

        for (int i = 0; i < DS.length(); i++) {
            Item data = DS.get(i);
            double dist = getSimilarity(x, data.getData()) * getSimilarityInverse(x, data.getData());
            result.add(new SimResult(data.getImdbId(), dist));
        }
        Collections.sort(result, (a, b) -> -Double.compare(a.accuarcy, b.accuarcy));

        return new LinkedList<>(result.subList(0, n));
    }

    public class SimResult {
        private int imdbID;
        private double accuarcy;

        public SimResult(int imdb, double acc) {
            imdbID = imdb;
            accuarcy = acc;
        }

        public int getImdb() {
            return imdbID;
        }

        public double getAccuarcy() {
            return accuarcy;
        }

        public String toString() {
            return String.format("Accuarcy : %f, Imdb Id : %d", accuarcy, imdbID);
        }
    }
}
