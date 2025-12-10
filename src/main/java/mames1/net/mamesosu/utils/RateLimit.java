package mames1.net.mamesosu.utils;

public interface RateLimit {

    default boolean checkNotExceeded(long lastActionTime, long currentTime) {

        if(lastActionTime != 0) {
            return Math.abs(currentTime - lastActionTime) >= 30;
        }

        return true;
    }
}
