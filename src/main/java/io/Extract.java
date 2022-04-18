package io;

import utils.Settings;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class Extract {

    private static final int MAX_SUBS_EXTRACTION = Integer.MAX_VALUE;
    private static final String TEMP_FOLDER = "tmp";

    public static void extract(Path path, int streamId, File out) throws IOException {
        boolean back = false;
        String ffmpeg = Settings.getInstace().getFfmpegPath();
        String[] cmd = new String[]{
                ffmpeg,
                "-loglevel",
                "8",
                "-progress",
                "pipe:1",
                "-y",
                "-i",
                path.toString(),
                "-map",
                "0:" + streamId,
                "-c:s",
                "copy",
                out.toString()
        };
        Process proc = new ProcessBuilder(cmd).start();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        String s = null;

        while ((s = stdInput.readLine()) != null) {
            if (s.equals("progress=continue")) {
                if (back) {
                    System.out.print("/\r");
                } else {
                    System.out.print("\\\r");
                }
                back = !back;
            }
        }
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
    }

    public static File[] extractAll(Path path) throws IOException {
        String ffmpeg = Settings.getInstace().getFfmpegPath();
        FfprobeResult[] subs = getSubtitleIds(path);
        String subsFolder = String.format("%s%s%s%s", TEMP_FOLDER, File.pathSeparator, path.getFileName().toString().replaceAll("\\.[^\\.]+$", ""), File.pathSeparator);
        Files.createDirectories(Paths.get(subsFolder));
        File[] result = new File[Math.min(subs.length, MAX_SUBS_EXTRACTION)];
        boolean back = false;
        ArrayList<String> cmdList = new ArrayList<>(Arrays.asList(ffmpeg + " -loglevel 8 -progress pipe:1 -y -i".split(" ")));
        cmdList.add(path.toString());

        for (int i = 0; i < result.length; i++) {
            String subDest = String.format("%s%d.sup", subsFolder, subs[i].streamID);
            cmdList.add("-map");
            cmdList.add(String.format("0:%d", subs[i].streamID));
            cmdList.add("-c:s");
            cmdList.add("copy");
            cmdList.add(subDest);
            result[i] = new File(subDest);
        }
        Process proc = new ProcessBuilder(cmdList.toArray(new String[cmdList.size()])).start();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        String s;
        while ((s = stdInput.readLine()) != null) {
            if (s.equals("progress=continue")) {
                if (back) {
                    System.out.print("/\r");
                } else {
                    System.out.print("\\\r");
                }
                back = !back;
            }
        }
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
        return result;
    }

    public static FfprobeResult[] getSubtitleIds(Path path) throws IOException {
        String ffprobe = Settings.getInstace().getFfprobePath();
        String[] cmd = new String[]{
                ffprobe,
                "-loglevel",
                "error",
                "-select_streams",
                "s",
                "-show_entries",
                "stream=index,codec_name",
                "-of",
                "csv=p=0",
                path.toString()
        };
        Process proc = new ProcessBuilder(cmd).start();
        BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        String s = null;
        LinkedList<FfprobeResult> liste = new LinkedList<>();
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }
        s = null;
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

    public static class FfprobeResult {

        public final int streamID;
        public final String name;

        public FfprobeResult(int stream, String name) {
            this.name = name;
            streamID = stream;
        }
    }
}
