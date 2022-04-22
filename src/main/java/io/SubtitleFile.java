package io;

import java.io.File;

public class SubtitleFile extends File {

    private final String language, type;

    public SubtitleFile(String pathname, String language, String type) {
        super(pathname);
        this.language = language;
        this.type = type;
    }
}
