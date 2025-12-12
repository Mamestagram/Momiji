package mames1.net.mamesosu.object.model;

public class BanchoBeatmap extends Beatmap {

    public int approved;
    public long hitLength;
    public double difficultyRating;
    public double aimRating;
    public double speedRating;
    public double cs;
    public double od;
    public double ar;
    public double hp;
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
            double aimRating,
            double speedRating,
            double cs,
            double od,
            double ar,
            double hp,
            double maxCombo,
            int countNormal,
            int countSlider
    ) {

        super(title, artist, version, beatmapId, beatmapSetId);

        this.approved = approved;
        this.hitLength = hitLength;
        this.difficultyRating = difficultyRating;
        this.aimRating = aimRating;
        this.speedRating = speedRating;
        this.cs = cs;
        this.od = od;
        this.ar = ar;
        this.hp = hp;
        this.maxCombo = maxCombo;
        this.countNormal = countNormal;
        this.countSlider = countSlider;
    }

    public boolean isNotAcceptedMap() {
        return !(hitLength >= 30 && (approved == -2 || approved >= 1) && od > 1);
    }
}
