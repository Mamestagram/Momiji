package mames1.net.mamesosu.object.model;

public class BanchoBeatmap extends Beatmap{

    public int approved;
    public long totalLength;
    public long hitLength;
    public String version;
    public double difficultyRating;
    public double diffDrain;
    public double maxCombo;

    public boolean isAcceptedMap() {
        return hitLength >= 30 && (approved == -2 || approved >= 1) && diffDrain > 1;
    }
}
