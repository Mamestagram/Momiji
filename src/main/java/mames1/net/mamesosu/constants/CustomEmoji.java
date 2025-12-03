package mames1.net.mamesosu.constants;

import lombok.Getter;

@Getter
public enum CustomEmoji {

    INFO("<:info:1285954858998042694>"),
    PEOPLEGROUP("<:peoplegroup:1285955898124140575>"),
    SCREEN("<:screencast:1286261681974018122>");

    public final String id;

    CustomEmoji(String id) {
        this.id = id;
    }
}
