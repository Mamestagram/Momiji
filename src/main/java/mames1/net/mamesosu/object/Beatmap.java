package mames1.net.mamesosu.object;

public class Beatmap {

    public String title;
    public String artist;
    public String version;
    public long beatmapId;
    public long beatmapSetId;

    public String getFullName() {
        return title + " -" + artist + " [" + version + "]";
    }
}
