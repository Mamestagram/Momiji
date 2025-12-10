package mames1.net.mamesosu.discord.monitor;

import com.fasterxml.jackson.databind.JsonNode;
import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.constants.emoji.CustomEmoji;
import mames1.net.mamesosu.constants.emoji.UnicodeEmoji;
import mames1.net.mamesosu.object.model.UrlAnalysis;
import mames1.net.mamesosu.utils.http.JsonHttpClient;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageUrlMonitor extends ListenerAdapter {

    private static void getRequestProperty(HttpURLConnection conn, String apiKey) throws IOException {

        conn.setRequestMethod("GET");
        conn.setRequestProperty("accept", "application/json");
        conn.setRequestProperty("x-apikey", apiKey);
        conn.setDoOutput(true);
    }

    private static JsonNode getUrlAnalysis(UrlAnalysis urlAnalysis) throws IOException {

        final URL analyzeUrl = new URL("https://www.virustotal.com/api/v3/analyses/" + urlAnalysis.id);
        JsonNode resultNode;

        HttpURLConnection conn = (HttpURLConnection) analyzeUrl.openConnection();
        getRequestProperty(conn, urlAnalysis.apiKey);
        resultNode = JsonHttpClient.getJsonNode(conn);

        if (resultNode == null) {
            return null;
        }

        if(!resultNode.has("data")) {
            return null;
        }

        if (!resultNode.get("data").has("attributes")) {
            return null;
        }

        return resultNode.get("data").get("attributes");
    }

    private static CompletableFuture<JsonNode> getUrlAnalysisAsync(UrlAnalysis urlAnalysis) {

        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        JsonNode resultNode;

                        do {
                            TimeUnit.SECONDS.sleep(1);
                            resultNode = getUrlAnalysis(urlAnalysis);
                        } while(resultNode == null);

                        return resultNode;

                    } catch (Exception e) {
                        AppLogger.log(e.getMessage(), LogLevel.ERROR);
                    }
                    return null;
                }
        );
    }

    private boolean isSafeUrl(String uri, String apiKey) throws Exception{

        final URL apiUrl = new URL("https://www.virustotal.com/api/v3/urls");
        HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
        String data = "url=" + uri;
        String id;
        int maliciousCount;
        int suspiciousCount;
        JsonNode resultNode;
        JsonNode attributeNode;
        JsonNode statsNode;
        UrlAnalysis urlAnalysis;

        getRequestProperty(conn, apiKey);

        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = data.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        resultNode = JsonHttpClient.getJsonNode(conn);

        if(resultNode == null) {
            return false;
        }

        if(!resultNode.has("data")) {
            return false;
        }

        if(!resultNode.get("data").has("id")) {
            return false;
        }

        id = resultNode.get("data").get("id").asText();
        conn.disconnect();

        urlAnalysis = new UrlAnalysis(id, apiKey);
        attributeNode = getUrlAnalysis(urlAnalysis);

        if(attributeNode == null) {
            return false;
        }

        if(attributeNode.get("results").isEmpty()) {
            attributeNode = getUrlAnalysisAsync(urlAnalysis).join();
        }

        if (attributeNode == null) {
            return false;
        }

        if(attributeNode.has("stats")) {
            statsNode = attributeNode.get("stats");

            if(statsNode.has("malicious")) {
                maliciousCount = statsNode.get("malicious").asInt();
                suspiciousCount = statsNode.get("suspicious").asInt();

                System.out.println(maliciousCount + " " + suspiciousCount);

                if(maliciousCount > 0) {
                    return false;
                }

                return suspiciousCount == 0;
            }
        }

        return false;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {

        final String urlRegex = "(https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+)";
        EmbedBuilder checkEmbed = new EmbedBuilder();
        Pattern urlPattern = Pattern.compile(urlRegex);
        String messageContent = e.getMessage().getContentRaw();
        Matcher matcher = urlPattern.matcher(messageContent);
        Message message = e.getMessage();
        String url;
        String apiKey = Main.bot.getVirusKey();

        checkEmbed.setTitle(CustomEmoji.WARNING.getId() + " **There may be a security issue!**");
        checkEmbed.setDescription("This message may contain a URL that could be unsafe. Please proceed at your own risk if you choose to access the link.");
        checkEmbed.addField("Content", messageContent, false);
        checkEmbed.setColor(Color.RED);

        if(e.getJDA().getSelfUser() == e.getAuthor()) {
            return;
        }

        if(matcher.find()) {
            message.addReaction(Emoji.fromUnicode(UnicodeEmoji.COUNTER_CLOCKWISE_ARROWS.getEmoji())).queue();
            url = matcher.group(1);

            try {
                if(!isSafeUrl(url, apiKey)) {
                    e.getMessage().replyEmbeds(
                            checkEmbed.build())
                            .queue(success -> e.getMessage().delete().queue());
                    return;
                }

                e.getMessage().removeReaction(Emoji.fromUnicode(UnicodeEmoji.COUNTER_CLOCKWISE_ARROWS.getEmoji())).queue(
                        success -> e.getMessage().addReaction(Emoji.fromUnicode(UnicodeEmoji.WHITE_HEAVY_CHECK_MARK.getEmoji())).queue()
                );

            } catch (Exception e1) {
                AppLogger.log(e1.getMessage(), LogLevel.ERROR);
            }
        }
    }
}
