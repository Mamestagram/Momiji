package mames1.net.mamesosu.server.monitor;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.constants.Channel;
import mames1.net.mamesosu.constants.Endpoint;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.constants.ServerRole;
import mames1.net.mamesosu.object.Bot;
import mames1.net.mamesosu.utils.http.CheckStatusClient;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.exceptions.ContextException;
import net.dv8tion.jda.api.exceptions.ErrorResponseException;

import java.awt.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// サーバーのヘルスモニタリングを行うクラス
// ダウンしてたらDiscordのボイスチャンネル名を変更と管理者に通知する
public class ServerHealthMonitor {

    public void startMonitoring() {

        Bot bot = Main.bot;
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        Map<String, Long> endpoints = Map.of(
                Endpoint.BANCHO.getUrl(), Channel.BANCHO_STATUS.getId(),
                Endpoint.WEB.getUrl(), Channel.WEB_STATUS.getId()
        );

        scheduler.scheduleAtFixedRate(
                () -> {
                    try {
                    for(int i = 0; i < endpoints.size(); i++){
                        // エンドポイントと対応するボイスチャンネルIDを取得
                        String endpoint = (String) endpoints.keySet().toArray()[i];
                        Long channelId = (Long) endpoints.values().toArray()[i];

                        // ボイスチャンネルとロールを取得
                        VoiceChannel channel = bot.getJda().getVoiceChannelById(channelId);
                        Role role = bot.getJda().getRoleById(ServerRole.FRONTEND_DEV.getId());

                        boolean isUp = CheckStatusClient.checkStatus(endpoint);

                        if(channel == null) {
                            AppLogger.log("指定されたチャンネルIDのボイスチャンネルが見つかりません: " + channelId, LogLevel.WARN);
                            continue;
                        }

                        if (role == null) {
                            AppLogger.log("指定されたロールIDのロールが見つかりません: " + ServerRole.FRONTEND_DEV.getId(), LogLevel.WARN);
                            continue;
                        }

                        Guild guild = role.getGuild();
                        String name = channel.getName();

                        List<Member> admins = guild.getMembersWithRoles(role);

                        if(isUp){
                            channel.getManager().setName(name.replace("Down", "Up")).queue();
                            continue;
                        }

                        // サービスがダウンしている場合の処理
                        EmbedBuilder notificationEmbed = new EmbedBuilder();

                        notificationEmbed.setTitle("サービスが停止している可能性があります!");
                        notificationEmbed.setDescription("この通知が間違いの場合は無視してください.");
                        notificationEmbed.addField("**停止している可能性のあるエンドポイント**",
                                endpoint, false);
                        notificationEmbed.setColor(Color.RED);
                        notificationEmbed.setTimestamp(new Date().toInstant());

                        for(Member member : admins){

                            if(member.getUser().getId().equals(bot.getJda().getSelfUser().getId())){
                                continue;
                            }

                                member.getUser().openPrivateChannel().queue(pc -> pc.sendMessageEmbeds(notificationEmbed.build()).queue(
                                        success -> AppLogger.log("管理者 " + member.getUser().getAsTag() + " にDMで通知を送信しました.", LogLevel.INFO),
                                        failure -> AppLogger.log("管理者 " + member.getUser().getAsTag() + " へのDM通知の送信に失敗しました: " + failure.getMessage(), LogLevel.INFO
                                )));
                        }

                        channel.getManager().setName(name.replace("Up", "Down")).queue();

                        AppLogger.log(endpoint + " is Down!!", LogLevel.WARN);
                    }
                    } catch (Exception e) {
                        AppLogger.log("エラーが発生しました: " + e.getMessage(), LogLevel.ERROR);
                    }
                },
                0,
                60,
                TimeUnit.SECONDS
        );
    }
}
