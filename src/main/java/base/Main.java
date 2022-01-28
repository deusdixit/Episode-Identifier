package base;

import cli.Runner;
import picocli.CommandLine;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;

public class Main {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Runner()).execute(args);
    }

    public static void draw(BitSet timestamps, String prefix) {
        int w = timestamps.length();
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
            ImageIO.write(image, "png", new File(prefix + "-" + String.format("org%09d.png", System.currentTimeMillis())));
        } catch (IOException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }
}
