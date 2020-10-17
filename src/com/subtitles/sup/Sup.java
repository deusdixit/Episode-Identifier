package com.subtitles.sup;

import com.google.gson.Gson;
import com.subtitles.sup.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Sup {

    private final int magicNumber = 0x5047;
    private final int endSequenceId = 0x80;
    private byte[] data;
    private PGS[] segments = null;

    public Sup(Path filePath) {
        try {
            data = Files.readAllBytes(filePath);
        } catch (IOException ioex) {
            System.out.println("IOException : " + ioex.getLocalizedMessage());
        }
    }

    public PGS[] getSegments() {
        if (segments == null) {
            parse();
        }
        return segments;
    }

    public void toJson(Path filepath) {
        if (segments == null) {
            parse();
        }
        Gson gson = new Gson();
        String json = gson.toJson(segments);
        try {
            Files.write(filepath, json.getBytes());
        } catch (IOException ioex) {
            System.out.println("IOException while writing json...");
            System.out.println(ioex.getMessage());
        }
    }

    public void parse() {
        List<PGS> objs = new ArrayList<>();
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
            case 0x14 -> obj = pds(index, obj);
            case 0x15 -> obj = ods(index, obj);
            case 0x16 -> obj = pcs(index, obj);
            case 0x17 -> obj = wds(index, obj);
            case 0x80 -> obj = end(index, obj);
            default -> System.out.println("Found unknown type : " + type);
        }
        System.out.println("Size of the segment : " + sizeOfSegment + " - Segment type : " + type);
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
            System.out.println("Segment Size doesn't fit");
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
            System.out.println("Segment Size doesn't fit");
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
            System.out.println("Segment Size doesn't fit");
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


}
