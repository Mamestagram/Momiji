package mames1.net.mamesosu.object.model;

import java.util.List;

public class BanchoBeatmapset {

    public List<BanchoBeatmap> beatmaps;

    public BanchoBeatmapset(List<BanchoBeatmap> beatmaps) {
        this.beatmaps = beatmaps;
    }

    public boolean isNotSpeedDiffBeatmapset() {

        int tmpNormalCount = beatmaps.get(0).countNormal;

        for (int i = 1; i < beatmaps.size(); i++) {

            if (tmpNormalCount != beatmaps.get(i).countNormal) {
                return false;
            }
        }

        return true;
    }

    public boolean isAcceptedMapSet() {

        for (BanchoBeatmap b : beatmaps) {
            if(b.isNotAcceptedMap()) {
                return false;
            }
        }

        return true;
    }
}
