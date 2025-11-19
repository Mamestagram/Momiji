package mames1.net.mamesosu.object;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.Getter;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.listener.BotReady;
import mames1.net.mamesosu.server.monitor.ActivePlayerMonitor;
import mames1.net.mamesosu.server.monitor.TopScoreMonitor;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

@Getter
public class Bot {

    JDA jda;

    public Bot() {
        Dotenv dotenv = Dotenv.configure().load();

        String token = dotenv.get("BOT_TOKEN");

        this.jda = JDABuilder.createDefault(token)
                .setRawEventsEnabled(true)
                .enableIntents(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT,
                        GatewayIntent.GUILD_MEMBERS,
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS
                ).enableCache(
                        CacheFlag.MEMBER_OVERRIDES,
                        CacheFlag.ROLE_TAGS,
                        CacheFlag.EMOJI
                )
                .disableCache(
                        CacheFlag.STICKER,
                        CacheFlag.SCHEDULED_EVENTS
                ).setActivity(
                        Activity.listening("Botを起動しています..")
                ).setMemberCachePolicy(
                        MemberCachePolicy.ALL
                ).setChunkingFilter(
                        ChunkingFilter.ALL
                ).addEventListeners(
                        new BotReady(),
                        new TopScoreMonitor(),
                        new ActivePlayerMonitor()
                ).build();

        AppLogger.log("Botを起動しました. トークンは: " + token, LogLevel.INFO);
    }
}
