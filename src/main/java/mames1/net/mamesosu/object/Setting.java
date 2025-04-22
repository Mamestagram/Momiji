package mames1.net.mamesosu.object;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class Setting {

    String secretKey;
    String domain;
    String beatmapMirror;

    long rankedMapForumChannelId;
    long rankedForumTagId;

    long bnOsuChannelId;
    long bnTaikoChannelId;
    long bnCatchChannelId;
    long bnManiaChannelId;

    long bnOsuRoleId;
    long bnTaikoRoleId;
    long bnCatchRoleId;
    long bnManiaRoleId;

    long guildId;

    // 自動キックするプレイヤー
    List<String> blacklist;

    public Setting () {
        blacklist = new ArrayList<>();
        Dotenv dotenv = Dotenv.configure().load();

        this.secretKey = dotenv.get("SECRET_KEY");
        this.domain = dotenv.get("DOMAIN");
        this.blacklist = List.of(dotenv.get("BLACKLIST").split(","));

        this.guildId = Long.parseLong(dotenv.get("GUILD_ID"));

        this.beatmapMirror = dotenv.get("BEATMAP_MIRROR");

        this.bnOsuChannelId = Long.parseLong(dotenv.get("BN_OSU_CHANNEL_ID"));
        this.bnTaikoChannelId = Long.parseLong(dotenv.get("BN_TAIKO_CHANNEL_ID"));
        this.bnCatchChannelId = Long.parseLong(dotenv.get("BN_CATCH_CHANNEL_ID"));
        this.bnManiaChannelId = Long.parseLong(dotenv.get("BN_MANIA_CHANNEL_ID"));
        this.bnOsuRoleId = Long.parseLong(dotenv.get("BN_OSU_ROLE_ID"));
        this.bnTaikoRoleId = Long.parseLong(dotenv.get("BN_TAIKO_ROLE_ID"));
        this.bnCatchRoleId = Long.parseLong(dotenv.get("BN_CATCH_ROLE_ID"));
        this.bnManiaChannelId = Long.parseLong(dotenv.get("BN_MANIA_ROLE_ID"));

        this.rankedMapForumChannelId = Long.parseLong(dotenv.get("RANKED_MAP_FORUM_CHANNEL_ID"));
        this.rankedForumTagId = Long.parseLong(dotenv.get("RANKED_FORUM_TAG_ID"));
    }
}
