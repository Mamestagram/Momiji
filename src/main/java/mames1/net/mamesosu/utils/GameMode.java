package mames1.net.mamesosu.utils;

public abstract class GameMode {

    public static String getModeToString (int mode) {
        switch (mode) {
            case 0 -> {
                return "vn!std";
            }
            case 1 -> {
                return "vn!taiko";
            }
            case 2 -> {
                return "vn!ctb";
            }
            case 3 -> {
                return "vn!mania";
            }
            case 4 -> {
                return "rx!std";
            }
            case 5 -> {
                return "rx!taiko";
            }
            case 6 -> {
                return "rx!ctb";
            }
            case 8 -> {
                return "ap!std";
            }
            default -> {
                return "Unknown!";
            }
        }
    }
}
