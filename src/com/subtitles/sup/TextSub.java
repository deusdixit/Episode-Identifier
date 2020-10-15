package com.subtitles.sup;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextSub {

    private List<String> data;
    private int[] timestamps = null;

    public TextSub(Path path) {
        System.out.println(path.toAbsolutePath());
        try {
            data = Files.readAllLines(path);
        } catch(IOException ioex) {
            System.out.println("IOException : " + ioex.getLocalizedMessage());
        }
    }

    public void parse() {
        List<String> allMatches = new ArrayList<>();
        for(String line : data ) {
            Matcher m = Pattern.compile("[0-9][0-9]:[0-9][0-9]:[0-9][0-9]").matcher(line);
            while (m.find()) {
                allMatches.add(m.group());
            }
        }
        timestamps = new int[allMatches.size()];
        int counter = 0;
        for(String line : allMatches ) {
            System.out.println(line);
            String[] split = line.split(":");
            int current = (Integer.parseInt(split[0]) * 60 * 60) + (Integer.parseInt(split[1]) * 60) + (Integer.parseInt(split[2]));
            timestamps[counter++] = current;
        }
    }

    public int[] getTimestamps() {
        if ( timestamps == null ) {
            parse();
        }
        return timestamps;
    }
}
