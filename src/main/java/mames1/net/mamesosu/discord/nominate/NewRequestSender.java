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
import mames1.net.mamesosu.utils.http.JsonHttpClient;
import mames1.net.mamesosu.utils.log.AppLogger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewRequestSender extends ListenerAdapter implements JsonHttpClient{

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
                        beatmap.get("diff_size").asDouble(),
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

        JDA jda = Main.bot.getJda();

        switch (mode) {
            case "osu" -> {
                return jda.getRoleById(ServerRole.BN_OSU.getId());
            }
            case "taiko" -> {
                return jda.getRoleById(ServerRole.BN_TAIKO.getId());
            }
            case "fruits" -> {
                return jda.getRoleById(ServerRole.BN_CATCH.getId());
            }
            case "mania" -> {
                return jda.getRoleById(ServerRole.BN_MANIA.getId());
            }
            default -> {
                return null;
            }
        }
    }

    private long getRequestChannelIdByMode(String mode) {
        switch (mode) {
            case "osu" -> {
                return Channel.REQ_OSU.getId();
            }
            case "taiko" -> {
                return Channel.REQ_TAIKO.getId();
            }
            case "fruits" -> {
                return Channel.REQ_CTB.getId();
            }
            case "mania" -> {
                return Channel.REQ_MANIA.getId();
            }
            default -> {
                return -1;
            }
        }
    }

    // フォームに入力されたリクエストを処理する
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

        if(e.getGuild() == null) {
            return;
        }

        if(!e.getModalId().contains("ranked")) {
            return;
        }

        if(e.getValues().isEmpty()) {
            return;
        }

        if(e.getValue("map_url") == null) {
            return;
        }

        try {

            // URLが正しい形式か確認
            matcher = pattern.matcher((Objects.requireNonNull(e.getValue("map_url")).getAsString()));

            if(matcher.find()) {
                mapRequest = new MapRequest(
                        matcher.group(2),
                        Integer.parseInt(matcher.group(3)),
                        Integer.parseInt(matcher.group(1)),
                        e.getMember()
                );

                // メンションを送る先のロールを取得
                bnRole = getRoleByMode(mapRequest.mode);
                if(getRequestChannelIdByMode(mapRequest.mode) == -1 || bnRole == null) {
                    e.replyEmbeds(
                            getErrorEmbed("Invalid game mode specified.").build()
                    ).setEphemeral(true).queue();
                    return;
                }

                // モード別のBNチャンネルを取得 (メッセージを送る先)
                bnChannel = e.getGuild().getTextChannelById(getRequestChannelIdByMode(mapRequest.mode));
                if (bnChannel == null) {
                    e.replyEmbeds(
                            getErrorEmbed("Failed to access the request channel. Please contact the staff.").build()
                    ).setEphemeral(true).queue();
                    return;
                }

                // マップセット全体の情報をBanchoから取得
                beatmapset = getBeatmapset(mapRequest.beatmapsetId);
                if (beatmapset == null) {
                    e.replyEmbeds(
                            getErrorEmbed("Failed to retrieve beatmap information from osu! API. Please ensure the URL is correct.").build()
                    ).setEphemeral(true).queue();
                    return;
                }

                // もし「all」が選択されていたら、速度が変更されている差分とRanked基準を満たしていないマップが含まれているか検証
                if(e.getModalId().contains("all")) {
                    if(!beatmapset.isNotSpeedDiffBeatmapset()) {
                        e.replyEmbeds(
                                getErrorEmbed("Ranked status for beatmaps with speed-altered difficulties is not allowed by the rules.\n" +
                                        "Only one normal-speed difficulty can be Ranked in a beatmapset.").build()
                        ).setEphemeral(true).queue();

                        return;
                    }

                    if (!beatmapset.isAcceptedMapSet()) {
                        e.replyEmbeds(
                                getErrorEmbed(
                                        "The map you requested does not meet the Ranked criteria.\n" +
                                                "Please check the Ranked requirements and submit the map again once it meets them."
                                ).build()
                        ).setEphemeral(true).queue();
                        return;
                    }
                }

                // 単体Diffリクエストの場合、該当DiffがRanked基準を満たしているか検証
                if(beatmapset.beatmaps.stream().noneMatch(b -> b.beatmapId == mapRequest.beatmapId && b.isNotAcceptedMap())) {
                    e.replyEmbeds(
                            getErrorEmbed(
                                    "The selected difficulty does not meet the Ranked criteria.\n" +
                                            "Please check the Ranked requirements and submit the map again once it meets them."
                            ).build()
                    ).setEphemeral(true).queue();
                    //return;
                }

                // WIP:
            }


        } catch (Exception ex) {
            AppLogger.log(ex.getLocalizedMessage(), LogLevel.ERROR);
        }

    }
}
