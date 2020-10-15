package com.subtitles.sup;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Sup {

    private byte[] data;
    private final int magicNumber = 0x5047;
    private final int endSequenceId = 0x80;
    private int[] timestamps = null;

    public Sup(Path filePath) {
        try {
            data = Files.readAllBytes(filePath);
        } catch (IOException ioex) {
            System.out.println("IOException : " + ioex.getLocalizedMessage());
        }
    }

    public void parse() {
        List<Integer> ts = new ArrayList<>();
        for(int i = 0; i < data.length-1;i++) {
            if ( magicNumber == ((data[i] & 0xff) << 8 | data[i+1] & 0xff)) {
                if ( (data[i+10] & 0xff) == endSequenceId ) {
                    int pTimestamp = new BigInteger(new byte[]{data[i + 2], data[i + 3], data[i + 4], data[i + 5]}).intValue();
                    pTimestamp /= 90; // 90kHz
                    Duration duration = Duration.ofMillis(pTimestamp);
                    String pretty = String.format("%02d:%02d:%02d", duration.toHours(),duration.toMinutesPart(),duration.toSecondsPart());
                    System.out.println("Timestamp : " + pretty);
                    ts.add((int)duration.toSeconds());
                }
            }
        }
        timestamps = new int[ts.size()];
        for(int i = 0 ; i < timestamps.length;i++) {
            timestamps[i] = ts.get(i);
        }
    }

    public int[] getTimestamps() {
        if ( timestamps == null ) {
            parse();
        }
        return timestamps;
    }



}
