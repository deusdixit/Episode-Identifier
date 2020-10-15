package com.subtitles.sup;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) {
        Path path = Paths.get(args[0]);
        Path path1 = Paths.get("/home/niels/Downloads/spa1.srt");
        Path path2 = Paths.get("/home/niels/Downloads/fre1.srt");
        Path path3 = Paths.get("/home/niels/Downloads/test.srt");
        //Path path4 = Paths.get("/home/niels/Downloads/spa1.srt");
        Sup sup = new Sup(path);
        draw(sup.getTimestamps());
        //TextSub tsub = new TextSub(path3);
        //tsub.parse();
        //draw(tsub.getTimestamps());
    }

    public static void draw(int[] timestamps) {
        int w = timestamps[timestamps.length-1];
        int h = 100;
        int type = BufferedImage.TYPE_INT_ARGB;

        BufferedImage image = new BufferedImage(w, h, type);



        int col1 = Color.BLACK.getRGB();

        int col2 = Color.WHITE.getRGB();
        int color;
        for(int x = 0; x < w; x++) {
            if (Arrays.binarySearch(timestamps,0,timestamps.length,x) >= 0 ) {
                color = col2;
            } else {
                color = col1;
            }
            for(int y = 0; y < h; y++) {
                image.setRGB(x, y, color);
            }
        }
        try {
            ImageIO.write(image, "png", new File("org.png"));
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }
}
