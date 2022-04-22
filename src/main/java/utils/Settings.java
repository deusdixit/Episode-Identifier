package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.prefs.Preferences;

public class Settings {

    private static Settings settings = null;

    private Preferences prefs;

    private final String FFMPEG_KEY = "ffmpeg_path";
    private final String FFPROBE_KEY = "ffprobe_path";
    private final String KEEP_TEMPORARY_KEY = "del_temp";

    private final String TEMPLATE_KEY = "template";
    private final String OS;

    public static Settings getInstace() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }
    public Settings() {
        prefs = Preferences.userRoot().node(getClass().getName());
        OS = System.getProperty("os.name");
    }

    public boolean isFfmpegValid() {
        try {
            Process p = new ProcessBuilder(getFfmpegPath(), "-version").start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String s = null;
            String output = "";
            while ((s = stdInput.readLine()) != null) {
                output += s;
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            if (output.contains("ffmpeg version")) {
                return true;
            }
        } catch (IOException ioe) {
            return false;
        }
        return false;
    }

    public boolean isFfprobeValid() {
        try {
            Process p = new ProcessBuilder(getFfprobePath(), "-version").start();
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String s = null;
            String output = "";
            while ((s = stdInput.readLine()) != null) {
                output += s;
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
            if (output.contains("ffprobe version")) {
                return true;
            }
        } catch (IOException ioe) {
            return false;
        }
        return false;
    }

    public String getFfmpegPath() {
        String defaultValue = "";
        if (OS.contains("win")) {
            if (cmdExists("ffmpeg")) {
                defaultValue = "ffmpeg";
            } else if (cmdExists("ffmpeg.exe")) {
                defaultValue = "ffmpeg.exe";
            }
        } else {
            if (cmdExists("/usr/bin/ffmpeg")) {
                defaultValue = "/usr/bin/ffmpeg";
            } else if (cmdExists("/bin/ffmpeg")) {
                defaultValue = "/bin/ffmpeg";
            } else if (cmdExists("/usr/local/bin/ffmpeg")) {
                defaultValue = "/usr/local/bin/ffmpeg";
            } else if (cmdExists("ffmpeg")) {
                defaultValue = "ffmpeg";
            }
        }
        return prefs.get(FFMPEG_KEY, defaultValue);
    }

    public void putFfmpegPath(String path) {
        prefs.put(FFMPEG_KEY, path);
    }

    public String getFfprobePath() {
        String defaultValue = "";
        if (OS.contains("win")) {
            if (cmdExists("ffprobe")) {
                defaultValue = "ffprobe";
            } else if (cmdExists("ffprobe.exe")) {
                defaultValue = "ffprobe.exe";
            }
        } else {
            if (cmdExists("/usr/bin/ffprobe")) {
                defaultValue = "/usr/bin/ffprobe";
            } else if (cmdExists("/bin/ffprobe")) {
                defaultValue = "/bin/ffprobe";
            } else if (cmdExists("/usr/local/bin/ffprobe")) {
                defaultValue = "/usr/local/bin/ffprobe";
            } else if (cmdExists("ffprobe")) {
                defaultValue = "ffprobe";
            }
        }
        return prefs.get(FFPROBE_KEY, defaultValue);
    }

    public void putFfprobePath(String path) {
        prefs.put(FFPROBE_KEY, path);
    }

    public boolean getKeepTemporary() {
        return prefs.getBoolean(KEEP_TEMPORARY_KEY, false);
    }

    public void putKeepTemporary(boolean value) {
        prefs.putBoolean(KEEP_TEMPORARY_KEY, value);
    }

    private boolean cmdExists(String cmd) {
        try {
            Process p = new ProcessBuilder(cmd).start();
            return true;
        } catch (IOException ioe) {
            return false;
        }
    }

    public String getTemplate() {
        return prefs.get(TEMPLATE_KEY, "{p}({y})-s{s}e{e}[[{tmdb-{t}}]]");
    }

    public void putTemplate(String temp) {
        prefs.put(TEMPLATE_KEY, temp);
    }

}
