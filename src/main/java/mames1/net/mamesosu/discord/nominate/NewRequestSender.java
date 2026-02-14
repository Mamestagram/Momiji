package mames1.net.mamesosu.discord.nominate;

import com.fasterxml.jackson.databind.JsonNode;
import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.constants.Channel;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.constants.ServerRole;
import mames1.net.mamesosu.constants.emoji.CustomEmoji;
import mames1.net.mamesosu.object.model.BanchoBeatmap;
import mames1.net.mamesosu.object.model.BanchoBeatmapset;
import mames1.net.mamesosu.object.model.MapRequest;
import mames1.net.mamesosu.utils.Render;
import mames1.net.mamesosu.utils.http.JsonHttpClient;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewRequestSender extends ListenerAdapter implements JsonHttpClient, Render {

    // ... (既存の getErrorEmbed, getBeatmapset, getRoleByMode, getRequestChannelIdByMode メソッドは変更なし) ...
    private EmbedBuilder getErrorEmbed(String reason) {
        EmbedBuilder errorEmbed = new EmbedBuilder();
        errorEmbed.setTitle(CustomEmoji.WARNING.getId() + " **Error**");
        errorEmbed.setDescription("An error occurred while processing the request. If this issue persists, please contact the developer.");
        errorEmbed.addField("Reason", reason, false);
        errorEmbed.setColor(Color.red);
        errorEmbed.setTimestamp(new Date().toInstant());
        return errorEmbed;
    }

    private BanchoBeatmapset getBeatmapset(int beatmapsetId) {
        // ... (省略: 元のコードと同じ) ...
        String banchoEndpoint = "https://osu.ppy.sh/api/get_beatmaps";
        JsonNode mapData;
        HttpURLConnection urlConnection;
        URL url;
        String banchoKey = Main.bot.getBanchoKey();
        List<BanchoBeatmap> beatmaps = new ArrayList<>();

        banchoEndpoint += "?k=" + banchoKey + "&s=" + beatmapsetId;

        try {
            url = new URL(banchoEndpoint);
            urlConnection = (HttpURLConnection) url.openConnection();

            mapData = getJsonNode(
                    urlConnection
            );

            if(mapData == null || mapData.isEmpty()) {
                return null;
            }

            for(JsonNode beatmap : mapData) {
                beatmaps.add(new BanchoBeatmap(
                        beatmap.get("title").asText(),
                        beatmap.get("artist").asText(),
                        beatmap.get("version").asText(),
                        beatmap.get("beatmap_id").asLong(),
                        beatmap.get("beatmapset_id").asLong(),
                        beatmap.get("approved").asInt(),
                        beatmap.get("hit_length").asInt(),
                        beatmap.get("difficultyrating").asDouble(),
                        beatmap.get("diff_aim").asDouble(),
                        beatmap.get("diff_speed").asDouble(),
                        beatmap.get("diff_size").asDouble(),
                        beatmap.get("diff_overall").asDouble(),
                        beatmap.get("diff_approach").asDouble(),
                        beatmap.get("diff_drain").asDouble(),
                        beatmap.get("max_combo").asInt(),
                        beatmap.get("count_normal").asInt(),
                        beatmap.get("count_slider").asInt()
                ));
            }

            return new BanchoBeatmapset(beatmaps);

        } catch (Exception e) {
            AppLogger.log(e.getLocalizedMessage(), LogLevel.ERROR);

            return null;
        }
    }

    private Role getRoleByMode(String mode) {
        // ... (省略: 元のコードと同じ) ...
        JDA jda = Main.bot.getJda();
        switch (mode) {
            case "osu" -> { return jda.getRoleById(ServerRole.BN_OSU.getId()); }
            case "taiko" -> { return jda.getRoleById(ServerRole.BN_TAIKO.getId()); }
            case "fruits" -> { return jda.getRoleById(ServerRole.BN_CATCH.getId()); }
            case "mania" -> { return jda.getRoleById(ServerRole.BN_MANIA.getId()); }
            default -> { return null; }
        }
    }

    private long getRequestChannelIdByMode(String mode) {
        // ... (省略: 元のコードと同じ) ...
        switch (mode) {
            case "osu" -> { return Channel.REQ_OSU.getId(); }
            case "taiko" -> { return Channel.REQ_TAIKO.getId(); }
            case "fruits" -> { return Channel.REQ_CTB.getId(); }
            case "mania" -> { return Channel.REQ_MANIA.getId(); }
            default -> { return -1; }
        }
    }


    @SuppressWarnings("unused")
    @Override
    public void onModalInteraction(ModalInteractionEvent e) {

        final String URLREGEX = "beatmapsets/(\\d+)#(osu|taiko|fruits|mania)/(\\d+)";
        Pattern pattern = Pattern.compile(URLREGEX);
        Matcher matcher;
        Role bnRole;
        EmbedBuilder responseEmbed = new EmbedBuilder();
        TextChannel bnChannel;
        MapRequest mapRequest;
        BanchoBeatmapset beatmapset;

        // ... (バリデーション部分は既存のコードと同じ) ...
        if(e.getGuild() == null || !e.getModalId().contains("ranked") || e.getValues().isEmpty() || e.getValue("map_url") == null) {
            return;
        }

        try {
            matcher = pattern.matcher((Objects.requireNonNull(e.getValue("map_url")).getAsString()));

            if(matcher.find()) {
                mapRequest = new MapRequest(
                        matcher.group(2),
                        Integer.parseInt(matcher.group(3)),
                        Integer.parseInt(matcher.group(1)),
                        e.getMember()
                );

                bnRole = getRoleByMode(mapRequest.mode);

                if(getRequestChannelIdByMode(mapRequest.mode) == -1 || bnRole == null) {
                    e.replyEmbeds(getErrorEmbed("Invalid game mode specified.").build()).setEphemeral(true).queue();
                    return;
                }

                bnChannel = e.getGuild().getTextChannelById(getRequestChannelIdByMode(mapRequest.mode));
                if (bnChannel == null) {
                    e.replyEmbeds(getErrorEmbed("Failed to access the request channel.").build()).setEphemeral(true).queue();
                    return;
                }

                beatmapset = getBeatmapset(mapRequest.beatmapsetId);
                if (beatmapset == null) {
                    e.replyEmbeds(getErrorEmbed("Failed to retrieve beatmap information.").build()).setEphemeral(true).queue();
                    return;
                }

                if(!e.getModalId().contains("all")) {
                    beatmapset.beatmaps.removeIf(b -> b.beatmapId != mapRequest.beatmapId);
                }

                if(beatmapset.isNotSpeedDiffBeatmapset()) {
                    e.replyEmbeds(getErrorEmbed("Ranked status for beatmaps with speed-altered difficulties is not allowed.").build()).setEphemeral(true).queue();
                    return;
                }

                if (!beatmapset.isAcceptedMapSet()) {
                    e.replyEmbeds(getErrorEmbed("The map you requested does not meet the Ranked criteria.").build()).setEphemeral(true).queue();
                    return;
                }

                // BNチャンネルにリクエスト通知を送信
                responseEmbed.setTitle(CustomEmoji.WARNING.getId() + " **A new request has arrived!**");
                responseEmbed.setDescription("A new Ranked request for a map has been submitted.\n" +
                        "Please review the map listed below.");

                responseEmbed.addField("Beatmap", "**[" + beatmapset.beatmaps.get(0).getFullName() + "]" +
                                "(https://osu.ppy.sh/beatmapsets/" + mapRequest.beatmapsetId + "#" + mapRequest.mode + "/" + mapRequest.beatmapId + ")**",
                        false);

                // 画像添付としてチャートを設定
                responseEmbed.setImage("attachment://beatmap_stats.png");
                responseEmbed.setColor(new Color(255, 102, 170)); // osu! pinkish color

                // **ここを変更: 綺麗なチャート（画像）を生成**
                BufferedImage image = beatmapset.createBeatmapInfoImage();
                Path tmp = Files.createTempFile("beatmap_stats_", ".png");

                try (OutputStream os = Files.newOutputStream(tmp)) {
                    ImageIO.write(image, "png", os);
                }

                File file = tmp.toFile();

                bnChannel.sendMessageEmbeds(responseEmbed.build())
                        .addFiles(
                                FileUpload.fromData(file, "beatmap_stats.png")
                        ).queue(
                                success -> {
                                    boolean flg = file.delete();
                                },
                                failure -> {
                                    boolean flg = file.delete();
                                }
                        );

                // ユーザーへの完了報告
                e.reply("Request sent successfully!").setEphemeral(true).queue();
            }

        } catch (Exception ex) {
            AppLogger.log(ex.getLocalizedMessage(), LogLevel.ERROR);
            e.replyEmbeds(
                    getErrorEmbed("An unexpected error occurred.").build()
            ).setEphemeral(true).queue();
        }
    }
}
