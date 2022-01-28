package io;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

public class DataSet implements Serializable {
    @Serial
    private static final long serialVersionUID = 4480600303123781401L;

    private ArrayList<Item> items;

    public DataSet() {
        items = new ArrayList<>();
    }

    public void add(Item i) {
        items.add(i);
    }

    public int length() {
        return items.size();
    }

    public Item get(int index) {
        return items.get(index);
    }

    public Item getByImdb(int imdb) {
        Collections.sort(items);
        int index = Collections.binarySearch(items, new Item(imdb, null));
        if (index >= 0) {
            return items.get(index);
        } else {
            return null;
        }
    }

}
