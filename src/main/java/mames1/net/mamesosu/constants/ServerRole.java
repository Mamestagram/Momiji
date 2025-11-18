package mames1.net.mamesosu.constants;

import lombok.Getter;

@Getter
public enum ServerRole {

    BACKEND_DEV(1378297360840523866L),
    FRONTEND_DEV(1086658038204735489L);

    final long id;

    ServerRole(long id) {
        this.id = id;
    }
}
