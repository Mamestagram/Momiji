package mames1.net.mamesosu.object;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import lombok.Setter;
import mames1.net.mamesosu.support.beatmap.CreateRequest;
import mames1.net.mamesosu.support.beatmap.Nominate;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Getter @Setter
public class Bot {

    String token;
    String version;
    JDA jda;

    public Bot() {
        Dotenv dotenv = Dotenv.configure().load();
        this.token = dotenv.get("TOKEN");
        this.version = dotenv.get("VERSION");
    }

    public void load() {
        jda = JDABuilder.createDefault(token)
                 .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS
                ).enableCache(
                        CacheFlag.MEMBER_OVERRIDES,
                        CacheFlag.ROLE_TAGS,
                        CacheFlag.EMOJI
                )
                .disableCache(
                        CacheFlag.VOICE_STATE,
                        CacheFlag.STICKER,
                        CacheFlag.SCHEDULED_EVENTS
                ).setActivity(
                        Activity.playing("Running Momiji v" + version)
                 ).setMemberCachePolicy(
                        MemberCachePolicy.ALL
                ).setChunkingFilter(
                        ChunkingFilter.ALL
                ).addEventListeners(new CreateRequest())
                .addEventListeners(new Nominate())
                .build();
    }
}
