package io;

import javafx.beans.property.SimpleIntegerProperty;

import java.io.Serializable;
import java.util.BitSet;

public class Item implements Comparable<Item>, Serializable {

    private SimpleIntegerProperty imdbId;
    private BitSet data;
    private SimpleIntegerProperty fileId;
    private static final long serialVersionUID = 4480600303123781401L;

    public Item(int imdb, int fileid, BitSet data) {
        this.imdbId = new SimpleIntegerProperty();
        this.data = data;
        this.fileId = new SimpleIntegerProperty();
        this.imdbId.set(imdb);
        this.fileId.set(fileid);
    }

    public SimpleIntegerProperty imdbIdProperty() {
        return imdbId;
    }

    public SimpleIntegerProperty fileIdProperty() {
        return fileId;
    }

    public int getImdbId() {
        return imdbId.getValue();
    }

    public int getFileId() {
        return fileId.getValue();
    }

    public BitSet getData() {
        return data;
    }

    @Override
    public int compareTo(Item o) {
        if (getImdbId() == o.getImdbId()) {
            return Integer.compare(getFileId(), o.getFileId());
        }
        return Integer.compare(getImdbId(), o.getImdbId());
    }
}
