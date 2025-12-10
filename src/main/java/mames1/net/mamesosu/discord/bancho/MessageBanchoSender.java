package mames1.net.mamesosu.discord.bancho;

import mames1.net.mamesosu.constants.Channel;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.object.Server;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MessageBanchoSender extends ListenerAdapter {

    private String removeUnsupportedString(String content) {
        final String emojiPattern = "[\\p{So}\\p{Cn}]+";
        final String urlPattern = "https?://\\S+";

        return content.replaceAll(urlPattern, "") // http/httpsのURLを除去
                .replaceAll(emojiPattern, "") // 絵文字を除去
                .trim();
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {

        final String endpoint = "https://api.mamesosu.net/v1/discord_webhook";
        Server server = new Server();
        String userName;
        String message;
        String secretKey = server.getPrivateKey();
        HttpURLConnection connection;
        URL url;

        if (e.getAuthor().isBot()) return;

        if (!e.getChannelType().isGuild()) return;

        if (!e.getMessage().getAttachments().isEmpty()) return;

        if (e.getChannel().getIdLong() != Channel.OSU_CHAT.getId()) return;

        if (e.getMember() == null) return;

        if(!e.getMessage().getEmbeds().isEmpty()) return;

        // メッセージからURLと絵文字を除去 -> UTF-8エンコード
        message = URLEncoder.encode(removeUnsupportedString(e.getMessage().getContentRaw()), StandardCharsets.UTF_8);
        userName = URLEncoder.encode(e.getMember().getEffectiveName(), StandardCharsets.UTF_8);

        if (message.isBlank()) return;

        try {

            url = new URL(endpoint + "?key=" + secretKey + "&name" + userName + "&content=" + message);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            connection.getInputStream();
            connection.disconnect();

        } catch (Exception ex) {
            AppLogger.log(ex.getMessage(), LogLevel.ERROR);
        }
    }
}
