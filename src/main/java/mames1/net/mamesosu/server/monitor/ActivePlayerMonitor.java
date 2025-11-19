package mames1.net.mamesosu.server.monitor;

import com.fasterxml.jackson.databind.JsonNode;
import mames1.net.mamesosu.constants.Channel;
import mames1.net.mamesosu.constants.Endpoint;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.utils.http.JsonHttpClient;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class ActivePlayerMonitor extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        JsonNode node;
        int activePlayers;

        if(!e.getChannelType().isGuild()) {
            return;
        }

        if(e.getChannel().getIdLong() != Channel.SERVER_LOG.getId()) {
            return;
        }

        try {

            node = JsonHttpClient.getJsonNode(Endpoint.BANCHO.getUrl());

            if(node == null) {
                e.getJDA().getPresence().setActivity(Activity.watching("Server looks down.."));
                return;
            }

            activePlayers = node.get("counts").get("online").asInt();

            e.getJDA().getPresence().setActivity(Activity.watching("Active players: " + activePlayers));

            AppLogger.log("プレイヤー人数を更新しました: " + activePlayers, LogLevel.INFO);

        } catch (Exception ex) {
            AppLogger.log("エラーが発生しました: " + ex.getMessage(), LogLevel.ERROR);
        }
    }
}
