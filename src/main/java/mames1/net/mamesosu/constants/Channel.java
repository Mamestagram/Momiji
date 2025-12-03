package mames1.net.mamesosu.constants;

import lombok.Getter;

// 開発用に変更しているので本番環境では注意
@Getter
public enum Channel {

    ANNOUNCE_EN(1012666760866041916L),
    ANNOUNCE_JP(1282237683275403277L),
    OSU_CHAT(1012876250399907850L),
    SERVER_LOG(1081737936401350717L),
    BANCHO_STATUS(1286709001744154757L),
    WEB_STATUS(1191969724431614054L),
    TOPPLAYS_POST(1093421523936755722L),
    ROLE_ASSIGNMENT(1012691799350988810L);

    final long id;

    Channel(long id) {
        this.id = id;
    }
}
