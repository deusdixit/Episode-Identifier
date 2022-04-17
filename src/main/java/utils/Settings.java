package utils;

import java.util.prefs.Preferences;

public class Settings {

    private static Settings settings = null;

    private Preferences prefs;

    private final String FFMPEG_KEY = "ffmpeg_path";
    private final String FFPROBE_KEY = "ffprobe_path";
    private final String KEEP_TEMPORARY_KEY = "del_temp";

    public static Settings getInstace() {
        if (settings == null) {
            settings = new Settings();
        }
        return settings;
    }

    public Settings() {
        prefs = Preferences.userRoot().node(getClass().getName());
    }

    public String getFfmpegPath() {
        return prefs.get(FFMPEG_KEY, "/usr/bin/ffmpeg");
    }

    public void putFfmpegPath(String path) {
        prefs.put(FFMPEG_KEY, path);
    }

    public String getFfprobePath() {
        return prefs.get(FFPROBE_KEY, "/usr/bin/ffprobe");
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

}
