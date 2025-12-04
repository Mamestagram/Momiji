package mames1.net.mamesosu.discord.monitor;

import com.fasterxml.jackson.databind.JsonNode;
import mames1.net.mamesosu.constants.emoji.UnicodeEmoji;
import mames1.net.mamesosu.utils.http.JsonHttpClient;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUrlMonitor extends ListenerAdapter {

    private boolean isSafeUrl(String uri, String apiKey) throws Exception{

        final URL apiUrl = new URL("https://www.virustotal.com/api/v3/urls");
        HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
        String data = "url=" + uri;
        JsonNode resultNode;

        conn.setRequestMethod("GET");
        conn.setRequestProperty("accept", "application/json");
        conn.setRequestProperty("x-apikey", apiKey);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        resultNode = JsonHttpClient.getJsonNode(conn);

        if(!resultNode.has("data")) {
            return false;
        }

        if(!resultNode.get("data").has("id")) {
            return false;
        }


    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        final String urlRegex = "(https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)";
        Pattern urlPattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        String messageContent = e.getMessage().getContentRaw();
        Matcher matcher = urlPattern.matcher(messageContent);
        Message message = e.getMessage();
        String url;

        if(e.getJDA().getSelfUser() == e.getAuthor()) {
            return;
        }

        if(matcher.find()) {
            message.addReaction(Emoji.fromUnicode(UnicodeEmoji.COUNTER_CLOCKWISE_ARROWS.getEmoji())).queue();
            url = matcher.group(1);

            try {

            }
        }
    }
}
