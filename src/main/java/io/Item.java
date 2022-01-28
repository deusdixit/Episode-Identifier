package io;

import java.io.Serializable;
import java.util.BitSet;

public class Item implements Comparable<Item>, Serializable {

    private int imdbId;
    private BitSet data;
    private static final long serialVersionUID = 4480600303123781401L;

    public Item(int imdb, BitSet data) {
        imdbId = imdb;
        this.data = data;
    }

    public int getImdbId() {
        return imdbId;
    }

    public BitSet getData() {
        return data;
    }

    @Override
    public int compareTo(Item o) {
        return Integer.compare(getImdbId(), o.getImdbId());
    }
}
