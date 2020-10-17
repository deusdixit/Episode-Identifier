package com.subtitles.sup.model;

public class PDS extends PGS {
    public final int type = 0x14;
    public int paletteId;
    public int paletteVersionNumber;
    public int paletteEntryId;
    public int luminanceY;
    public int colorDifferenceRedCr;
    public int colorDifferenceBlueCb;
    public int transparencyAlpha;

}
