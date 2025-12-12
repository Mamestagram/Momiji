package mames1.net.mamesosu.utils;

import java.awt.*;
import java.awt.image.BufferedImage;

public interface Render {

    default BufferedImage renderTextToImage(String text) {
        // フォント設定
        Font font = new Font("Consolas", Font.PLAIN, 14); // 等幅体を推奨
        BufferedImage tmp = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = tmp.createGraphics();
        g2d.setFont(font);
        FontMetrics fm = g2d.getFontMetrics();

        // テキストを行ごとに分割して幅と高さを計算
        String[] lines = text.split("\n");
        int lineHeight = fm.getHeight();
        int width = 0;
        for (String line : lines) {
            width = Math.max(width, fm.stringWidth(line));
        }
        int height = lineHeight * lines.length + 10;

        g2d.dispose();

        // 実際の画像を作成
        BufferedImage image = new BufferedImage(width + 20, height, BufferedImage.TYPE_INT_RGB);
        g2d = image.createGraphics();
        g2d.setFont(font);
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, image.getWidth(), image.getHeight());
        g2d.setColor(Color.BLACK);

        int y = fm.getAscent() + 5;
        for (String line : lines) {
            g2d.drawString(line, 10, y);
            y += lineHeight;
        }

        g2d.dispose();
        return image;
    }
}
