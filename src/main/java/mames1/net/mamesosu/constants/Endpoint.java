package mames1.net.mamesosu.constants;

import lombok.Getter;

@Getter
public enum Endpoint {

    BANCHO("https://api.mamesosu.net/v1/get_player_count"),
    WEB("https://web.mamesosu.net/");

    final String url;

    Endpoint(String url) {
        this.url = url;
    }
}
