package io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import subtitles.text.TextSubtitle;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class Dataloader {

    private static final Logger log = LoggerFactory.getLogger(Dataloader.class);

    public static int[][] loadInt(Path path) {
        try {
            List<Path> srtFiles = Files.walk(path).filter(p -> p.toFile().isFile() && p.toString().endsWith("srt")).collect(Collectors.toList());
            //srtFiles = srtFiles.subList(0, srtFiles.size() > 1000 ? 1000 : srtFiles.size());
            int[][] data = new int[srtFiles.size()][];
            int i = 0;
            for (Path p : srtFiles) {
                TextSubtitle tsub = new TextSubtitle(p);
                data[i++] = tsub.getTimestamps();
                log.debug(String.format("[%09d/%09d]", i, srtFiles.size()));
            }
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static DataSet loadSrt(Path path, boolean recursive) {
        return loadSrt(path, recursive, null);
    }

    public static DataSet loadFile(Path path, DataSet ds) {
        TextSubtitle tsub = new TextSubtitle(path);
        int imdbid;
        int fileid;
        try {
            imdbid = Integer.parseInt(path.getFileName().toString().split("\\.")[0].split("-")[1]);
            fileid = Integer.parseInt(path.getFileName().toString().split("\\.")[0].split("-")[2]);
        } catch (NumberFormatException ex) {
            log.error("Error reading IMDB from file " + path.toString());
            return ds;
        }
        if (!ds.contains(imdbid, fileid)) {
            ds.add(new Item(imdbid, fileid, null, tsub.getTimeMask()));
        }
        return ds;
    }

    public static DataSet loadSrt(Path path, boolean recursive, DataSet ds) {
        if (ds == null) {
            ds = new DataSet();
        }
        try {
            List<Path> srtFiles = Files.walk(path, recursive ? Integer.MAX_VALUE : 1).filter(p -> p.toFile().isFile() && p.toString().endsWith("srt")).collect(Collectors.toList());
            int bitCount = 0;
            int i = 0;
            for (Path p : srtFiles) {
                loadFile(p, ds);
                i++;
                log.debug(String.format("[%09d/%09d]", i, srtFiles.size()));
            }
            //System.out.println(String.format("%d Kilobytes", bitCount / 8 / 1024));
            return ds;
        } catch (IOException e) {
            log.error(e.getLocalizedMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static DataSet load(Path path) throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(path.toFile());
        ObjectInputStream in = new ObjectInputStream(fileIn);
        DataSet e = (DataSet) in.readObject();
        in.close();
        return e;
    }

    public static void save(DataSet ds, Path path) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(path.toFile());
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(ds);
        out.close();
        fileOut.close();
    }
}
