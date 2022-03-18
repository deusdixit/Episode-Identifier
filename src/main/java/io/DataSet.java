package io;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    public boolean contains(int imdb) {
        return getByImdb(imdb).size() > 0;
    }

    public ArrayList<Item> get() {
        return items;
    }

    public boolean contains(int imdb, int fileid) {
        ArrayList<Item> items = getByImdb(imdb);
        return Collections.binarySearch(items, new Item(imdb, fileid, null)) >= 0;
    }

    public ArrayList<Item> getByImdb(int imdb) {
        Collections.sort(items);
        int index = Collections.binarySearch(items, new Item(imdb, 0, null), new Comparator<Item>() {
            @Override
            public int compare(Item item, Item t1) {
                return Integer.compare(item.getImdbId(), t1.getImdbId());
            }
        });
        ArrayList<Item> result = new ArrayList<>();
        if (index >= 0) {
            for (int i = index; i >= 0; i--) {
                if (items.get(i).getImdbId() == imdb) {
                    result.add(items.get(i));
                } else {
                    break;
                }
            }
            for (int i = index + 1; i < items.size(); i++) {
                if (items.get(i).getImdbId() == imdb) {
                    result.add(items.get(i));
                } else {
                    break;
                }
            }
        }
        Collections.sort(result);
        return result;
    }

}