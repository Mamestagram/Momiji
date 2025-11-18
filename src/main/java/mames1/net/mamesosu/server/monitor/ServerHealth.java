package mames1.net.mamesosu.server.monitor;

import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.constants.Channel;
import mames1.net.mamesosu.constants.Endpoint;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.constants.ServerRole;
import mames1.net.mamesosu.object.Bot;
import mames1.net.mamesosu.utils.http.CheckStatusClient;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

// サーバーのヘルスモニタリングを行うクラス
// ダウンしてたらDiscordのボイスチャンネル名を変更と管理者に通知する
public class ServerHealth {

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

                        String name = channel.getName();
                        List<Member> admins = role.getGuild().getMembers();

                        if(isUp){
                            channel.getManager().setName(name.replace("Down", "Up")).queue();
                            continue;
                        }

                        for(Member member : admins){
                            member.getUser().openPrivateChannel().queue(pc -> pc.sendMessage(endpoint + "がダウンしています。メンテナンスの場合は無視してください。").queue());
                        }

                        channel.getManager().setName(name.replace("Up", "Down")).queue();
                    }
                    } catch (Exception e) {
                        AppLogger.log(e.getMessage(), LogLevel.ERROR);
                    }
                },
                0,
                60,
                TimeUnit.SECONDS
        );
    }
}
