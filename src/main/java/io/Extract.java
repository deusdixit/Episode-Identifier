package io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.LinkedList;

public class Extract {

    public static void extractAll(Path path) throws IOException {
        FfprobeResult[] subs = getSubtitleIds(path);

        for (int i = 0; i < subs.length; i++) {
            Process proc = Runtime.getRuntime().exec(String.format("ffmpeg -i %s -map 0:%d -c:s copy %d.sup", path.toString(), subs[i].streamID, subs[i].streamID));
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            String s = null;
            while ((s = stdInput.readLine()) != null) {
                System.out.println(s);
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }
    }

    private static FfprobeResult[] getSubtitleIds(Path path) throws IOException {
        Process proc = Runtime.getRuntime().exec(String.format("ffprobe -loglevel error -select_streams s -show_entries stream=index,codec_name -of csv=p=0 %s", path.toString()));
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        String s = null;
        LinkedList<FfprobeResult> liste = new LinkedList<>();
        while ((s = stdInput.readLine()) != null) {
            if (s.matches("[0-9]+,.*")) {
                String[] spl = s.split(",");
                liste.add(new FfprobeResult(Integer.parseInt(spl[0]), spl[1]));
            }
        }
        FfprobeResult[] result = new FfprobeResult[liste.size()];
        int counter = 0;
        for (FfprobeResult obj : liste) {
            result[counter++] = obj;
        }
        return result;
    }

    private static class FfprobeResult {

        public final int streamID;
        public final String name;

        public FfprobeResult(int stream, String name) {
            this.name = name;
            streamID = stream;
        }
    }
}
