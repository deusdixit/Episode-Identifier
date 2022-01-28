package subtitles.sup.model;

public class PCS extends PGS {
    public final int type = 0x16;
    public int width;
    public int height;
    public int frameRate;
    public int compositionNumber;
    public int compositionState;
    public int paletteUpdateFlag;
    public int paletteId;
    public int numberOfCompositionObjects;

    public int objectId;
    public int windowId;
    public int objectCroppedFlag;
    public int objectHorizontalPosition;
    public int objectVerticalPostion;
    public int objectCropppingHorizontalPostion;
    public int objectCroppingVerticalPostion;
    public int objectCroppingWidth;
    public int objectCroppingHeight;
}
