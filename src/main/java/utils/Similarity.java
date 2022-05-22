package utils;

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

    public BitSet shift(BitSet x, int num) {
        BitSet result = new BitSet();
        for (int i = 0; i < x.length(); i++) {
            if (i + num >= 0 && x.get(i)) {
                result.set(i + num);
            }
        }
        return result;
    }

    public LinkedList<SimResult> getNeighbors(BitSet x, int n) {
        LinkedList<SimResult> result = new LinkedList<>();

        for (int i = 0; i < DS.length(); i++) {
            Item data = DS.get(i);
            double lengthDelta = 1.0 - (Math.abs(x.length() - data.getData().length()) / (double) Math.max(x.length(), data.getData().length()));
            double bestDist = 0.0;
            for (int j = -100; j <= 100; j++) {
                BitSet y = shift(data.getData(), j);
                double dist = lengthDelta * getSimilarity(x, y) * getSimilarityInverse(x, y);
                if (dist > bestDist) {
                    bestDist = dist;
                }
            }
            result.add(new SimResult(data.getImdbId(), bestDist));
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
