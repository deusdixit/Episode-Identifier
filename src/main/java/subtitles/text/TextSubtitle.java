package subtitles.text;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSubtitle {

    private List<String> data;
    private int[] timestamps = null;
    private BitSet TimeMask = null;

    private static final Logger log = LoggerFactory.getLogger(TextSubtitle.class);

    public TextSubtitle(Path path) {
        try {
            data = Files.readAllLines(path);
        } catch (IOException ioex) {
            log.error("IOException TextSubtitle(Path)");
        }
    }

    private int getTime(String str) {
        String timestamp = str.substring(0, str.indexOf(","));
        String[] parts = timestamp.split(":");
        return (Integer.parseInt(parts[0]) * 60 * 60) + (Integer.parseInt(parts[1]) * 60) + (Integer.parseInt(parts[2]));
    }

    public void parse() {
        List<String> allMatches = new ArrayList<>();
        for (String line : data) {
            Matcher m = Pattern.compile("[0-9][0-9]:[0-9][0-9]:[0-9][0-9],[0-9]{1,3}\\s*-->\\s*[0-9][0-9]:[0-9][0-9]:[0-9][0-9],[0-9]{1,3}").matcher(line);
            while (m.find()) {
                allMatches.add(m.group());
            }
        }
        //LinkedList<Integer> timings = new LinkedList<>();
        //int counter = 0;
        //int minus = 0;
        //int start = -1;
        //int end = -1;
        TimeMask = new BitSet();
        for (String line : allMatches) {
            line = line.replace(" ", "");
            String[] split = line.split("-->");
            int a = getTime(split[0]);
            int b = getTime(split[1]);
            for (int i = a; i <= b; i++) {
                TimeMask.set(i);
            }
            /*for(int i = 0; i < split.length;i++) {
                String timestamp = split[i].substring(0,split[i].indexOf(","));
                String[] parts = timestamp.split(":");
                int current = (Integer.parseInt(parts[0]) * 60 * 60) + (Integer.parseInt(parts[1]) * 60) + (Integer.parseInt(parts[2]));
                timings.add(current);

                *//*
                if ( start < 0 ) {
                    start = current;
                    continue;
                }
                if ( end < 0 ) {
                    end = current;
                    continue;
                }
                if ( i == 0 && Math.abs(end-current) < 2 ) {
                    end = -1;
                    continue;
                } else {
                    timings.add(end-start);
                    start = current;
                    end = -1;
                    continue;
                }*//*

            }
        }
        if ( start >= 0 && end >= 0 ) {
            timings.add(end-start);
        }
        timestamps = new int[timings.size()];
        for(int v : timings) {
            timestamps[counter++] = v;
        }*/
        }
    }

    private int nextPositiv(int[] arr, int index) {
        for (int i = index; i < arr.length; i += 2) {
            if (arr[i] >= 0) {
                return i;
            }
        }
        return -1;
    }

    private int[] cleanUp(int[] arr) {
        int counter = 0;
        for (int i = 1; i < arr.length - 1; i += 2) {
            int x = arr[i];
            if (x < 0) {
                continue;
            }
            int y = arr[i + 1];
            if (y < 0) {
                continue;
            }
            if (Math.abs(x - y) < 2) {
                arr[i + 1] = -1;
                arr[i] = -1;
                counter += 2;
                i = 1;
            }
        }
        int[] result = new int[arr.length - counter];
        counter = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] >= 0) {
                result[counter++] = arr[i];
            }
        }
        int[] distance = new int[result.length - 1];
        for (int i = 1; i < result.length; i++) {
            distance[i - 1] = result[i] - result[i - 1];
        }
        return distance;
    }

    public int[] getTimestamps() {
        if (timestamps == null) {
            parse();
        }
        return timestamps;
    }

    public BitSet getTimeMask() {
        if (TimeMask == null) {
            parse();
        }
        return TimeMask;
    }
}
