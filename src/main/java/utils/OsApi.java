package utils;

import gui.controller.MainController;
import gui.exceptions.NoOpensubtitlesException;
import id.gasper.opensubtitles.Opensubtitles;

public class OsApi {

    private static Opensubtitles instance = null;
    private static final String API_KEY = "9t8TuCJNE6AUBw0M7tlYDUVpmtwSHH8L";

    public static Opensubtitles getInstance() throws NoOpensubtitlesException {
        if (instance == null) {
            throw new NoOpensubtitlesException(MainController.getInstance());
        }
        return instance;
    }

    public static void setInstance(Opensubtitles os) {
        instance = os;
    }

    public static String getApiKey() {
        return API_KEY;
    }

    public static boolean isLoggedIn() {
        return instance != null && instance.isLoggedIn();
    }


}
