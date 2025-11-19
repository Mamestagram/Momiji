package mames1.net.mamesosu.utils;

public abstract class RateLimit {

    public static boolean checkNotExceeded(long lastActionTime, long currentTime) {

        if(lastActionTime != 0) {
            return Math.abs(currentTime - lastActionTime) >= 30;
        }

        return true;
    }
}
