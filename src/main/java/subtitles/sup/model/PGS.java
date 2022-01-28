package subtitles.sup.model;

public class PGS {
    public int presentationTimestamp; // timestamps have an accuracy of 90 kHz... devide by 90 to get the timestamp in milliseconds
    public int decodingTimestamp;
    public int sizeOfSegment;
}
