package mames1.net.mamesosu.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public interface Date {

    DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static String now() {
        return LocalDateTime.now(ZoneId.of("Asia/Tokyo")).format(FMT);
    }
}