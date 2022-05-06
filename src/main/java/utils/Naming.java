package utils;

import io.AttributesWrapper;
import io.Item;
import javafx.beans.InvalidationListener;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Naming {

    private static Naming instance = null;
    private final LinkedList<InvalidationListener> listener;

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
            Matcher m = Pattern.compile("\\[\\[([^\\[]+)]]").matcher(pattern);
            while (m.find(0)) {
                String match = m.group(1);
                String before = pattern.substring(0, m.start());
                String after = pattern.substring(m.end());
                for (String[] strings : map) {
                    if (match.contains(strings[0])) {
                        if (strings[1].length() > 0) {
                            match = match.replaceAll(strings[0], strings[1]);
                        } else {
                            match = "";
                            break;
                        }
                    }
                }
                pattern = before + match + after;
                m.reset(pattern);
            }
            for (String[] strings : map) {
                pattern = pattern.replaceAll(strings[0], strings[1]);
            }
        }
        return pattern;
    }

    public void addListener(InvalidationListener il) {
        listener.add(il);
    }
}
