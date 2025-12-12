package mames1.net.mamesosu.object.model;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BanchoBeatmapset {

    public List<BanchoBeatmap> beatmaps;

    public BanchoBeatmapset(List<BanchoBeatmap> beatmaps) {
        this.beatmaps = beatmaps;
    }

    public boolean isNotSpeedDiffBeatmapset() {
        int tmpNormalCount;

        if(beatmaps.size() < 2) {
            return true;
        }

        tmpNormalCount = beatmaps.get(0).countNormal;

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


    public String buildBeatmapsetsTable() {
        StringBuilder sb = new StringBuilder();

        // ヘッダー行(難易度名)
        sb.append(String.format("%-18s", "項目"));
        for (BanchoBeatmap b : beatmaps) {
            sb.append(String.format("%-14s", b.version)); // 難易度名など
        }
        sb.append("\n");

        BiConsumer<String, Function<BanchoBeatmap, Object>> row = (label, getter) -> {
            sb.append(String.format("%-18s", label));
            for (BanchoBeatmap b : beatmaps) {
                sb.append(String.format("%-14s", getter.apply(b)));
            }
            sb.append("\n");
        };

        row.accept("HitLength",     b -> b.hitLength);
        row.accept("DiffRating",    b -> b.difficultyRating);
        row.accept("AimRating",     b -> b.aimRating);
        row.accept("SpeedRating",   b -> b.speedRating);
        row.accept("CS",            b -> b.cs);
        row.accept("OD",            b -> b.od);
        row.accept("AR",            b -> b.ar);
        row.accept("HP",            b -> b.hp);
        row.accept("MaxCombo",      b -> b.maxCombo);
        row.accept("CountNormal",   b -> b.countNormal);
        row.accept("CountSlider",   b -> b.countSlider);

        return sb.toString();
    }
}
