package lsh;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class LSH {

    private final int ShingleSize;
    private final int SignatureSize;
    private final int[][] Data;
    private ArrayList<Integer> ShuffleList = null;
    private HashMap<Shingle, HashSet<Integer>> Table = null;

    public LSH(int shingleSize, int signatureSize, int[][] data) {
        ShingleSize = shingleSize;
        SignatureSize = signatureSize;
        Data = data;
    }

    private String token(int v) {
        return String.format("%010d", v);
    }

    public long[] generateRandomSeeds() {
        long[] seeds = new long[SignatureSize];
        Random rnd = ThreadLocalRandom.current();
        for (int i = 0; i < seeds.length; i++) {
            //seeds[i] = rnd.nextLong();
            seeds[i] = i;
        }
        return seeds;
    }

    public int[] getSignature(int[] data, long[] seeds) {
        HashMap<Shingle, HashSet<Integer>> hm = getTable();
        int id = hm.size();
        int[] result = new int[SignatureSize];
        addToTable(hm, data, id);
        Shingle[] keys = hm.keySet().toArray(new Shingle[hm.size()]);
        for (int i = 0; i < seeds.length; i++) {
            Random rnd = new Random(seeds[i]);
            while (true) {
                int index = rnd.nextInt(hm.size());
                if (hm.get(keys[index]).contains((Integer) id)) {
                    result[i] = index;
                    break;
                }
            }
        }
        return result;
    }

    public int[][] buildHashTable(long[] seeds) {
        HashMap<Shingle, HashSet<Integer>> hm = getTable();
        Shingle[] keys = hm.keySet().toArray(new Shingle[hm.size()]);
        int[][] result = new int[Data.length][SignatureSize];
        for (int i = 0; i < result.length; i++) {
            Arrays.fill(result[i], -1);
        }
        for (int i = 0; i < seeds.length; i++) {
            Random rnd = new Random(seeds[i]);
            int counter = 0;
            while (counter < result.length) {
                int index = rnd.nextInt(hm.size());
                for (int obj : hm.get(keys[index])) {
                    if (result[obj][i] < 0) {
                        result[obj][i] = index;
                        counter++;
                    }
                }
            }
            System.out.println(String.format("[%03d/%03d]", i, seeds.length));
        }

        return result;
    }

    private HashMap<Shingle, HashSet<Integer>> getTable() {
        if (Table == null) {
            Table = new HashMap<>();
            for (int i = 0; i < Data.length; i++) {
                addToTable(Table, Data[i], i);
            }
        }
        return Table;
    }

    private void addToTable(HashMap<Shingle, HashSet<Integer>> table, int[] data, int index) {
        Shingle el = new Shingle(ShingleSize);
        for (int j = 0; j < data.length; j++) {
            if (!el.isFull()) {
                el.add(data[j]);
            } else {
                el = el.addRolling(data[j]);
            }
            if (el.isFull()) {
                HashSet<Integer> hs = table.get(el);
                if (hs == null) {
                    hs = new HashSet<>();
                    hs.add(index);
                    table.put(el, hs);
                } else {
                    hs.add(index);
                }
            }
        }
    }

    public Integer[] getNeighbors(int[] sig, int[][] table, double threshold) {
        LinkedList<Integer> list = new LinkedList<>();
        for (int i = 0; i < table.length; i++) {
            if (getHammingDistance(sig, table[i]) > threshold) {
                list.add(i);
            }
        }
        return list.toArray(new Integer[list.size()]);
    }

    private double getHammingDistance(int[] dataA, int[] dataB) {
        double same = 0.0;
        for (int i = 0; i < dataA.length; i++) {
            if (dataA[i] == dataB[i]) {
                same++;
            }
        }
        return same / dataA.length;
    }
}
