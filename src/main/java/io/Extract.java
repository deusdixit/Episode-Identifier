package io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

public class Extract {

    private static int MAX_SUBS_EXTRACTION = Integer.MAX_VALUE;

    public static void extract(Path path, int streamId, File out) throws IOException {
        boolean back = false;

        String[] cmd = new String[]{
                "ffmpeg",
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
        FfprobeResult[] subs = getSubtitleIds(path);
        String subsFolder = String.format("./tmp/%s/", path.getFileName().toString().replaceAll("\\.[^\\.]+$", ""));
        Files.createDirectories(Paths.get(subsFolder));
        File[] result = new File[Math.min(subs.length, MAX_SUBS_EXTRACTION)];
        boolean back = false;
        for (int i = 0; i < result.length; i++) {
            String[] cmd = new String[]{
                    "ffmpeg",
                    "-loglevel",
                    "8",
                    "-progress",
                    "pipe:1",
                    "-y",
                    "-i",
                    path.toString(),
                    "-map",
                    "0:" + subs[i].streamID,
                    "-c:s",
                    "copy",
                    subsFolder + subs[i].streamID + ".sup"
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
            result[i] = new File(String.format("%s%d.sup", subsFolder, subs[i].streamID));
        }
        return result;
    }

    public static FfprobeResult[] getSubtitleIds(Path path) throws IOException {
        String[] cmd = new String[]{
                "ffprobe",
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
