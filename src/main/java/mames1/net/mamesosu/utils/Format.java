package mames1.net.mamesosu.utils;

public abstract class Format {

    public static String getConvertFormatTime(int seconds) {

        int minutes = seconds / 60;
        int remainingSeconds = seconds % 60;

        return String.format("%d:%02d", minutes, remainingSeconds);
    }
}
