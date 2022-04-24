package utils;

import io.AttributesWrapper;
import io.Item;
import javafx.beans.InvalidationListener;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Naming {

    private static Naming instance = null;
    private LinkedList<InvalidationListener> listener;

    public Naming() {
        listener = new LinkedList<>();
    }

    public static Naming getInstance() {
        if (instance == null) {
            instance = new Naming();
        }
        return instance;
    }

    public void fireOnChange() {
        for (InvalidationListener cl : listener) {
            cl.invalidated(null);
        }
    }

    public String getName(Item item) {
        return getName(item.getAttributeWrapper());
    }

    public String getName(AttributesWrapper item) {
        String pattern = Settings.getInstace().getTemplate();
        if (item != null) {
            String[][] map = new String[][]{{"\\{p}", item.getParentTitle()},
                    {"\\{e}", String.format("%02d", item.getEpisodeNumber())},
                    {"\\{s}", String.format("%02d", item.getSeasonNumber())},
                    {"\\{t}", String.valueOf(item.getTmbdId())},
                    {"\\{y}", String.valueOf(item.getYear())},
                    {"\\{i}", String.valueOf(item.getImdb())}};
            Matcher m = Pattern.compile("\\[\\[([^\\[]+)\\]\\]").matcher(pattern);
            while (m.find(0)) {
                String match = m.group(1);
                String before = pattern.substring(0, m.start());
                System.out.println("ENDE : " + m.end() + " PATTERN LENGTH : " + pattern.length());
                String after = pattern.substring(m.end(), pattern.length());
                for (int i = 0; i < map.length; i++) {
                    if (match.contains(map[i][0])) {
                        if (map[i][1].length() > 0) {
                            match.replaceAll(map[i][0], map[i][1]);
                        } else {
                            match = "";
                            break;
                        }
                    }
                }
                pattern = before + match + after;
                m.reset(pattern);
            }
            for (int i = 0; i < map.length; i++) {
                pattern = pattern.replaceAll(map[i][0], map[i][1]);
            }
        }
        return pattern;
    }

    public void addListener(InvalidationListener il) {
        listener.add(il);
    }
}
