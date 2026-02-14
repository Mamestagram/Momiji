package mames1.net.mamesosu.object.model;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.List;

public class BanchoBeatmapset {

    public List<BanchoBeatmap> beatmaps;

    public BanchoBeatmapset(List<BanchoBeatmap> beatmaps) {
        this.beatmaps = beatmaps;
    }

    public boolean isNotSpeedDiffBeatmapset() {
        int tmpNormalCount;

        if (beatmaps.size() < 2) {
            return false;
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
            if (b.isNotAcceptedMap()) {
                return false;
            }
        }

        return true;
    }

    public BufferedImage createBeatmapInfoImage() {
        int rowHeight = 30;
        int headerHeight = 40;
        int width = 700;
        int height = headerHeight + (beatmaps.size() * rowHeight);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // アンチエイリアス設定
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // 背景
        g2d.setColor(new Color(30, 30, 35));
        g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, 15, 15));

        // ヘッダー描画
        g2d.setColor(new Color(50, 50, 55));
        g2d.fillRect(0, 0, width, headerHeight);

        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("SansSerif", Font.BOLD, 14));

        int col1 = 20;  // Difficulty Name
        int col2 = 300; // Star Rating
        int col3 = 400; // CS
        int col4 = 450; // AR
        int col5 = 500; // OD
        int col6 = 550; // HP
        int col7 = 600; // Length

        g2d.drawString("Difficulty Name", col1, 25);
        g2d.drawString("Star Info", col2, 25);
        g2d.drawString("CS", col3, 25);
        g2d.drawString("AR", col4, 25);
        g2d.drawString("OD", col5, 25);
        g2d.drawString("HP", col6, 25);
        g2d.drawString("Len", col7, 25);

        // 行描画
        g2d.setFont(new Font("SansSerif", Font.PLAIN, 13));
        FontMetrics fm = g2d.getFontMetrics();

        for (int i = 0; i < beatmaps.size(); i++) {
            BanchoBeatmap map = beatmaps.get(i);
            int y = headerHeight + (i * rowHeight) + 20;
            int rowY = headerHeight + (i * rowHeight);

            // 縞模様の背景
            if (i % 2 == 0) {
                g2d.setColor(new Color(40, 40, 45));
                g2d.fillRect(0, rowY, width, rowHeight);
            }

            g2d.setColor(Color.LIGHT_GRAY);

            // Diff Name (長い場合は省略)
            String diffName = map.version;
            if (fm.stringWidth(diffName) > 260) {
                while(fm.stringWidth(diffName + "...") > 260 && !diffName.isEmpty()) {
                    diffName = diffName.substring(0, diffName.length() - 1);
                }
                diffName += "...";
            }
            g2d.drawString(diffName, col1, y);

            // SR (色付き)
            g2d.setColor(new Color(255, 204, 34)); // Gold/Yellow
            g2d.drawString(String.format("%.2f★", map.difficultyRating), col2, y);

            g2d.setColor(Color.WHITE);
            g2d.drawString(String.format("%.1f", map.cs), col3, y);
            g2d.drawString(String.format("%.1f", map.ar), col4, y);
            g2d.drawString(String.format("%.1f", map.od), col5, y);
            g2d.drawString(String.format("%.1f", map.hp), col6, y);

            // Length (分:秒)
            int min = (int)map.hitLength / 60;
            int sec = (int)map.hitLength % 60;
            g2d.drawString(String.format("%d:%02d", min, sec), col7, y);
        }

        g2d.dispose();
        return image;
    }
}
