package mames1.net.mamesosu.object.model;

public class BanchoBeatmap extends Beatmap {

    public int approved;
    public long hitLength;
    public double difficultyRating;
    public double diffDrain;
    public double maxCombo;
    public int countNormal;
    public int countSlider;

    public BanchoBeatmap(
            String title,
            String artist,
            String version,
            long beatmapId,
            long beatmapSetId,
            int approved,
            long hitLength,
            double difficultyRating,
            double diffDrain,
            double maxCombo,
            int countNormal,
            int countSlider
    ) {

        super(title, artist, version, beatmapId, beatmapSetId);

        this.approved = approved;
        this.hitLength = hitLength;
        this.difficultyRating = difficultyRating;
        this.diffDrain = diffDrain;
        this.maxCombo = maxCombo;
        this.countNormal = countNormal;
        this.countSlider = countSlider;
    }

    public boolean isNotAcceptedMap() {
        return !(hitLength >= 30 && (approved == -2 || approved >= 1) && diffDrain > 1);
    }
}
