package mames1.net.mamesosu.constants.emoji;

import lombok.Getter;

@Getter
public enum UnicodeEmoji {

    COUNTER_CLOCKWISE_ARROWS("U+1F504"),
    WHITE_HEAVY_CHECK_MARK("U+2705"),;

    final String emoji;

    UnicodeEmoji(String emoji) {
        this.emoji = emoji;
    }
}
