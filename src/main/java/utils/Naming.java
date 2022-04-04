package utils;

import io.AttributesWrapper;
import io.Item;

public class Naming {


    public static String getName(String template, Item item) {
        return getName(template, item.getAttributeWrapper());
    }

    public static String getName(String template, AttributesWrapper item) {
        if (item != null) {
            template = template.replace("{p}", item.getParentTitle());
            template = template.replace("{e}", String.format("%02d", item.getEpisodeNumber()));
            template = template.replace("{s}", String.format("%02d", item.getSeasonNumber()));
            template = template.replace("{t}", String.valueOf(item.getTmbdId()));
            //template = template.replace("{i}",String.valueOf(item));
            template = template.replace("{y}", String.valueOf(item.getYear()));
        }

        return template;
    }
}
