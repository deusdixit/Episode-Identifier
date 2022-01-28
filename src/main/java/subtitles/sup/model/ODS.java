package subtitles.sup.model;

public class ODS extends PGS {
    public final int type = 0x15;
    public int objectId;
    public int objectVersionNumber;
    public int lastInSequenceFlag;
    public int objectDataLength; // 3 bytes
    public int width;
    public int height;
    public String objectDataPath;
}
