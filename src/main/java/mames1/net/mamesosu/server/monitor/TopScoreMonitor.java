package mames1.net.mamesosu.server.monitor;


import mames1.net.mamesosu.constants.Channel;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.constants.ModeEmoji;
import mames1.net.mamesosu.constants.RankEmoji;
import mames1.net.mamesosu.object.Score;
import mames1.net.mamesosu.utils.GameMode;
import mames1.net.mamesosu.utils.Mods;
import mames1.net.mamesosu.utils.RateLimit;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.util.Calendar;
import java.util.Date;

// トップスコアを監視するクラス
// スコアを受信する度にDiscordの特定チャンネルのメッセージを更新する
public class TopScoreMonitor extends ListenerAdapter {

    int latestSendTime = 0;
    boolean isFirstRun = true;

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        long latestId;
        TextChannel postChannel;
        Message latestMessage;
        EmbedBuilder scoreEmbed = new EmbedBuilder();
        Calendar now = Calendar.getInstance();

        if(!e.getChannelType().isGuild()) {
            return;
        }

        if(e.getChannel().getIdLong() != Channel.SERVER_LOG.getId()) {
            return;
        }

        if(!RateLimit.checkNotExceeded(latestSendTime, now.get(Calendar.SECOND))) {
            return;
        }

        latestSendTime = now.get(Calendar.SECOND);
        postChannel = e.getJDA().getTextChannelById(Channel.TOPPLAYS_POST.getId());

        if(postChannel == null) {
            AppLogger.log("指定されたチャンネルIDのテキストチャンネルが見つかりません: " + Channel.SERVER_LOG.getId(), LogLevel.WARN);
            return;
        }

        latestId = postChannel.getLatestMessageIdLong();
        latestMessage = postChannel.retrieveMessageById(latestId).complete();
        isFirstRun = !(latestMessage.getAuthor().getIdLong() == e.getJDA().getSelfUser().getIdLong());

        scoreEmbed.setTitle("**<:ranking:1286182419455545396> Mamestagram Top Plays**", "https://web.mamesosu.net/leaderboard/std/performance");

        for(int i = 0; i <= 8; i++) {

            Score score = new Score();

            if (i == 7) continue;

            score = score.getTopScoreFromUserId(i);

            scoreEmbed.addField("**" + ModeEmoji.getModeEmojiByMode(i) + " " +
                    GameMode.getModeToString(i) + "** :flag_" + score.country + ": **" + score.userName + " (" + String.format("%.2f", score.pp) + "pp)**",
                    "* [" + score.beatmap.getFullName() + "](https://web.mamesosu.net/beatmaps/" + score.beatmap.beatmapSetId + "/" + score.beatmap.beatmapId + ") +" + Mods.getModsToString((int) score.mods) + "\n" +
                    "* " + RankEmoji.getRankEmojiByRank(score.grade) + " **" + String.format("%,d", score.score) + "score**", false);

            scoreEmbed.setColor(Color.BLACK);
            scoreEmbed.setTimestamp(new Date().toInstant());
        }

        if(isFirstRun) {
            postChannel.sendMessageEmbeds(scoreEmbed.build()).queue();
            isFirstRun = false;

            AppLogger.log("トップスコアを送信しました.", LogLevel.INFO);
            return;
        }

        latestMessage.editMessageEmbeds(scoreEmbed.build()).queue();

        AppLogger.log("トップスコアを更新しました.", LogLevel.INFO);
    }
}
