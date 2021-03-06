package subtitles.sup;

import com.google.gson.Gson;
import io.SubtitleFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import subtitles.Subtitle;
import subtitles.sup.model.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.*;

public class Sup extends Subtitle {

    private final int magicNumber = 0x5047;
    private final int endSequenceId = 0x80;
    private byte[] data;
    private PGS[] segments = null;
    private Map<Integer, BufferedImage> allImages;

    private static final Logger log = LoggerFactory.getLogger(Sup.class);

    public Sup(SubtitleFile sFile) {
        try {
            data = Files.readAllBytes(sFile.toPath());
        } catch (IOException ioex) {
            log.error("IOException Sup(Path)");
        }
    }

    public PGS[] getSegments() {
        if (segments == null) {
            parse();
        }
        return segments;
    }

    public BitSet getTimeMask() {
        BitSet result = new BitSet();
        int a = -1, b = -1;
        for (PGS p : getSegments()) {
            if (p instanceof PCS) {
                PCS pcs = (PCS) p;
                if (a < 0 && pcs.compositionState == 128) {
                    a = pcs.presentationTimestamp / 90000;
                    continue;
                } else if (a > 0 && b < 0 && pcs.compositionState == 0) {
                    b = pcs.presentationTimestamp / 90000;
                } else if (a < 0 && pcs.compositionState == 64) {
                    a = pcs.presentationTimestamp / 90000;
                    continue;
                } else {
                    continue;
                }
                for (int i = a; i <= b; i++) {
                    result.set(i);
                }
                a = -1;
                b = -1;
            }
        }
        return result;
    }

    public Map<Integer, BufferedImage> getImages() {
        if (segments == null) {
            parse();
        }
        return allImages;
    }

    public void toJson(String filename) {
        Path filepath = Paths.get(filename + ".json");
        if (segments == null) {
            parse();
        }
        Gson gson = new Gson();

        try {
            //String[] keys = allImages.keySet().toArray(new String[0]);
            File folder = new File(filename + "_images");
            if (!folder.exists()) {
                if (!folder.mkdir()) {
                    log.error("Couldn't create new folder for image files...");
                    System.exit(1);
                }
            }
            for (PGS segment : segments) {
                if (segment instanceof ODS && allImages.containsKey(segment.presentationTimestamp)) {

                    BufferedImage image = allImages.get(segment.presentationTimestamp);
                    ImageIO.write(image, "png", new File(filename + "_images/" + (segment.presentationTimestamp / 90.0 / 1000.0) + ".png"));
                    ((ODS) segment).objectDataPath = filename + "_images/" + segment.presentationTimestamp + ".png";
                }

            }
            String json = gson.toJson(segments);
            Files.write(filepath, json.getBytes());
        } catch (IOException ioex) {
            log.error("IOException while writing json...");
            log.error(ioex.getMessage());
        }
    }

    public void toJsonWithoutImages(String filename) {
        Path filepath = Paths.get(filename + ".json");
        if (segments == null) {
            parse();
        }
        Gson gson = new Gson();

        try {
            String json = gson.toJson(segments);
            Files.write(filepath, json.getBytes());
        } catch (IOException ioex) {
            System.out.println("IOException while writing json...");
            System.out.println(ioex.getMessage());
        }
    }

    private void parse() {
        List<PGS> objs = new ArrayList<>();
        allImages = new TreeMap<>();
        for (int i = 0; i < data.length - 1; i++) {
            if (magicNumber == ((data[i] & 0xff) << 8 | data[i + 1] & 0xff)) {
                PGS obj = pgs(i + 2);
                i = obj.sizeOfSegment + i + 12;
                objs.add(obj);
            }
        }
        segments = new PGS[objs.size()];
        for (int i = 0; i < segments.length; i++) {
            segments[i] = objs.get(i);
        }
    }

    private PGS pgs(int index) {
        int pTimestamp = (data[index] & 0xff) << 24 | (data[index + 1] & 0xff) << 16 | (data[index + 2] & 0xff) << 8 | (data[index + 3] & 0xff);
        index += 4;
        int dTimestamp = (data[index] & 0xff) << 24 | (data[index + 1] & 0xff) << 16 | (data[index + 2] & 0xff) << 8 | (data[index + 3] & 0xff);
        index += 4;
        int type = data[index] & 0xff;
        int sizeOfSegment = (data[index + 1] & 0xff) << 8 | (data[index + 2] & 0xff);
        index += 3;
        PGS obj = new PGS();
        obj.decodingTimestamp = dTimestamp;
        obj.presentationTimestamp = pTimestamp;
        obj.sizeOfSegment = sizeOfSegment;

        switch (type) {
            case 0x14:
                obj = pds(index, obj);
                break;
            case 0x15:
                obj = ods(index, obj);
                break;
            case 0x16:
                obj = pcs(index, obj);
                break;
            case 0x17:
                obj = wds(index, obj);
                break;
            case 0x80:
                obj = end(index, obj);
                break;
            default:
                log.warn("Found unknown type : " + type);
                break;
        }
        //System.out.println("Size of the segment : " + sizeOfSegment + " - Segment type : " + type);
        return obj;
    }

    private PCS pcs(int index, PGS obj) {
        PCS result = new PCS();
        result.sizeOfSegment = obj.sizeOfSegment;
        result.presentationTimestamp = obj.presentationTimestamp;
        result.decodingTimestamp = obj.decodingTimestamp;
        int left = obj.sizeOfSegment;
        if (left <= 0) {
            return result;
        }
        result.width = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.height = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.frameRate = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }

        result.compositionNumber = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.compositionState = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }

        result.paletteUpdateFlag = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }

        result.paletteId = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }

        result.numberOfCompositionObjects = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }

        result.objectId = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.windowId = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }

        result.objectCroppedFlag = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }

        result.objectHorizontalPosition = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.objectVerticalPostion = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.objectCropppingHorizontalPostion = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.objectCroppingVerticalPostion = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.objectCroppingWidth = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.objectCroppingHeight = readNBytes(index, 2);
        left -= 2;
        if (left > 0) {
            //System.err.println("Segment Size doesn't fit");
        }
        return result;

    }

    private WDS wds(int index, PGS obj) {
        WDS result = new WDS();
        result.sizeOfSegment = obj.sizeOfSegment;
        result.presentationTimestamp = obj.presentationTimestamp;
        result.decodingTimestamp = obj.decodingTimestamp;
        int left = obj.sizeOfSegment;
        if (left <= 0) {
            return result;
        }
        result.numberOfWindows = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }

        result.windowId = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }

        result.windowHorizontalPosition = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.windowVerticalPostion = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.windowWidth = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.windowHeight = readNBytes(index, 2);
        left -= 2;
        if (left > 0) {
            //System.out.println("Segment Size doesn't fit");
        }
        return result;
    }

    private PDS pds(int index, PGS obj) {
        PDS result = new PDS();
        result.sizeOfSegment = obj.sizeOfSegment;
        result.presentationTimestamp = obj.presentationTimestamp;
        result.decodingTimestamp = obj.decodingTimestamp;
        int left = obj.sizeOfSegment;
        if (left <= 0) {
            return result;
        }
        result.paletteId = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }
        result.paletteVersionNumber = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }
        result.paletteEntryId = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }
        result.luminanceY = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }
        result.colorDifferenceRedCr = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }
        result.colorDifferenceBlueCb = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }

        result.transparencyAlpha = readNBytes(index, 1);
        left--;
        if (left > 0) {
            //System.out.println("Segment Size doesn't fit");
        }
        return result;
    }

    private ODS ods(int index, PGS obj) {
        ODS result = new ODS();
        result.sizeOfSegment = obj.sizeOfSegment;
        result.presentationTimestamp = obj.presentationTimestamp;
        result.decodingTimestamp = obj.decodingTimestamp;
        int left = obj.sizeOfSegment;
        if (left <= 0) {
            return result;
        }
        result.objectId = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.objectVersionNumber = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }

        result.lastInSequenceFlag = readNBytes(index, 1);
        index++;
        left--;
        if (left <= 0) {
            return result;
        }

        result.objectDataLength = readNBytes(index, 3);
        index += 3;
        left -= 3;
        if (left <= 0) {
            return result;
        }

        result.width = readNBytes(index, 2);
        index += 2;
        left -= 2;
        if (left <= 0) {
            return result;
        }

        result.height = readNBytes(index, 2);

        index += 2;
        try {
            //BufferedImage image = getImages(index, result.objectDataLength, result.width, result.height);
            //allImages.put(result.presentationTimestamp, image);
        } catch (ArrayIndexOutOfBoundsException ex) {

        }
        return result;

    }

    private BufferedImage getImages(int index, int length, int width, int height) {
        BufferedImage result = new BufferedImage(width + 1, height + 1, BufferedImage.TYPE_INT_ARGB);
        int[] xy = new int[]{0, 0};
        int counter = 0;
        for (int i = 0; i < length; ) {
            int begin = readNBytes(index + i, 1);
            i++;
            if (begin == 0) {
                int snd = readNBytes(i + index, 1);
                i++;

                if (snd == 0) {
                    //System.out.println("x : " + xy[0] + " y : " + xy[1] + " --- " + width + " : " + height);
                    xy[0] = 0;
                    xy[1]++;
                } else if (nthBit(snd, 7) == 1 && nthBit(snd, 6) == 1) {
                    int anzahl = readNBytes(index + i - 1, 2) - 16384 - 32768;
                    i++;
                    int col = readNBytes(index + i, 1);
                    i++;
                    for (int j = 0; j < anzahl; j++) {
                        Color c = new Color(col, col, col);
                        result.setRGB(xy[0], xy[1], c.getRGB());
                        //System.out.println("x : " + xy[0] + " y : " + xy[1] + " --- " + width + " : " + height);
                        xy[0] = (xy[0] + 1) % (width + 1);
                    }
                } else if (nthBit(snd, 6) == 1) {
                    int anzahl = readNBytes(index + i - 1, 2) - 16384;
                    i++;
                    for (int j = 0; j < anzahl; j++) {
                        Color c = new Color(0, 0, 0);
                        result.setRGB(xy[0], xy[1], c.getRGB());
                        //System.out.println("x : " + xy[0] + " y : " + xy[1] + " --- " + width + " : " + height);
                        xy[0] = (xy[0] + 1) % (width + 1);
                    }
                } else if (nthBit(snd, 7) == 1) {
                    int col = readNBytes(index + i, 1);
                    i++;
                    for (int j = 0; j < snd - 128; j++) {
                        Color c = new Color(col, col, col);
                        //System.out.println("x : " + xy[0] + " y : " + xy[1] + " --- " + width + " : " + height);
                        result.setRGB(xy[0], xy[1], c.getRGB());
                        xy[0] = (xy[0] + 1) % (width + 1);
                    }
                } else {
                    for (int j = 0; j < snd; j++) {
                        Color c = new Color(0, 0, 0);
                        result.setRGB(xy[0], xy[1], c.getRGB());
                        //System.out.println("x : " + xy[0] + " y : " + xy[1] + " --- " + width + " : " + height);
                        xy[0] = (xy[0] + 1) % (width + 1);
                    }
                }
            } else {
                Color c = new Color(begin, begin, begin);
                result.setRGB(xy[0], xy[1], c.getRGB());
                //System.out.println("x : " + xy[0] + " y : " + xy[1] + " --- " + width + " : " + height);
                xy[0] = (xy[0] + 1) % (width + 1);
            }
        }
        return result;
    }

    private END end(int index, PGS obj) {
        END result = new END();
        result.sizeOfSegment = obj.sizeOfSegment;
        result.presentationTimestamp = obj.presentationTimestamp;
        result.decodingTimestamp = obj.decodingTimestamp;
        return result;
    }

    private int readNBytes(int index, int n) {
        int result = 0;
        int counter = 0;
        for (int i = n - 1; i >= 0; i--) {
            result |= (data[index + counter++] & 0xff) << (i * 8);
        }
        return result;
    }

    private int bytesToInt(byte[] b) {
        int result = 0;
        int counter = 0;
        for (int i = b.length - 1; i >= 0; i--) {
            result |= (b[counter++] & 0xff) << (i * 8);
        }
        return result;
    }

    private int nthBit(int b, int n) {
        return (b >> n) & 1;
    }

    private byte[] readBytes(int start, int n) {
        byte[] result = new byte[n];
        if (start + n - start >= 0) System.arraycopy(data, start, result, 0, start + n - start);
        return result;
    }


}
