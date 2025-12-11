package mames1.net.mamesosu.object.model;

public class Beatmap {

    public String title;
    public String artist;
    public String version;
    public long beatmapId;
    public long beatmapSetId;

    public Beatmap(
            String title,
            String artist,
            String version,
            long beatmapId,
            long beatmapSetId
    ) {
        this.title = title;
        this.artist = artist;
        this.version = version;
        this.beatmapId = beatmapId;
        this.beatmapSetId = beatmapSetId;
    }

    public String getFullName() {
        return title + " -" + artist + " [" + version + "]";
    }
}
