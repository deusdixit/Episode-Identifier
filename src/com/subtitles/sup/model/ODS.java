package com.subtitles.sup.model;

public class ODS extends PGS {

    public short objectId;
    public byte objectVersionNumber;
    public byte lastInSequenceFlag;
    public int objectDataLength; // 3 bytes
    public short width;
    public short height;
    public String objectDataPath;
}
