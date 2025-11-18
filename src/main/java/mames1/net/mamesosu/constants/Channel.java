package mames1.net.mamesosu.constants;

import lombok.Getter;

@Getter
public enum Channel {

    SERVER_LOG(1081737936401350717L),
    BANCHO_STATUS(1293033542699188357L),
    WEB_STATUS(1293033371538292776L);

    final long id;

    Channel(long id) {
        this.id = id;
    }
}
