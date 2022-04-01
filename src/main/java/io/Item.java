package io;

import java.io.Serializable;
import java.util.BitSet;

public class Item implements Comparable<Item>, Serializable {

    private int imdbId;
    private BitSet data;
    private int fileId;
    private AttributesWrapper aWrapper;
    private static final long serialVersionUID = 4480600303123781401L;

    public Item(int imdb, int fileid, AttributesWrapper aw, BitSet data) {
        this.data = data;
        this.imdbId = imdb;
        this.fileId = fileid;
        this.aWrapper = aw;
    }

    public int getImdbId() {
        return imdbId;
    }

    public int getFileId() {
        return fileId;
    }

    public BitSet getData() {
        return data;
    }

    public AttributesWrapper getAttributeWrapper() {
        return aWrapper;
    }

    @Override
    public int compareTo(Item o) {
        if (getImdbId() == o.getImdbId()) {
            return Integer.compare(getFileId(), o.getFileId());
        }
        return Integer.compare(getImdbId(), o.getImdbId());
    }
}
