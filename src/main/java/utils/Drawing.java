package utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;

public class Drawing {

    public static void draw(BitSet timestamps, File filename) {
        int w = Math.max(3600, timestamps.length());
        int height = 1000;
        int type = BufferedImage.TYPE_INT_ARGB;

        BufferedImage image = new BufferedImage(w, height, type);
        int col1 = Color.BLACK.getRGB();
        int col2 = Color.WHITE.getRGB();
        int color = col1;
        int last = 0;
        for (int i = 0; i < timestamps.length(); i++) {
            for (int h = 0; h < height; h++) {
                if (timestamps.get(i)) {
                    image.setRGB(i, h, col1);
                } else {
                    image.setRGB(i, h, col2);
                }
            }
        }
        try {
            ImageIO.write(image, "png", filename);
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }
}
