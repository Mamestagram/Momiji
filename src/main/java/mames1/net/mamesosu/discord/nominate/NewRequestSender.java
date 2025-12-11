package mames1.net.mamesosu.discord.nominate;

import com.fasterxml.jackson.databind.JsonNode;
import mames1.net.mamesosu.Main;
import mames1.net.mamesosu.constants.Channel;
import mames1.net.mamesosu.constants.LogLevel;
import mames1.net.mamesosu.constants.ServerRole;
import mames1.net.mamesosu.constants.emoji.CustomEmoji;
import mames1.net.mamesosu.object.MySQL;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
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

    private boolean isEligibleBeatmap(int beatmapId) {
        String banchoEndpoint = "https://osu.ppy.sh/api/get_beatmaps";
        JsonNode mapData;
        HttpURLConnection urlConnection;
        URL url;
        String banchoKey = Main.bot.getBanchoKey();

        banchoEndpoint += "?k=" + banchoKey + "&b=" + beatmapId;

        try {
            url = new URL(banchoEndpoint);
            urlConnection = (HttpURLConnection) url.openConnection();

            mapData = getJsonNode(
                    urlConnection
            );

            if(mapData == null || mapData.isEmpty()) {
                return false;
            }

            

        } catch (Exception e) {
            AppLogger.log(e.getLocalizedMessage(), LogLevel.ERROR);

            return false;
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

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {

        final String URLREGEX = "beatmapsets/(\\d+)#(osu|taiko|fruits|mania)/(\\d+)";
        MySQL db = new MySQL();
        Pattern pattern = Pattern.compile(URLREGEX);
        Matcher matcher;
        Role bnRole;
        EmbedBuilder responseEmbed = new EmbedBuilder();
        TextChannel bnChannel;
        MapRequest mapRequest;
        Connection connection;
        ResultSet result;

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

            connection = db.getConnection();
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
                    e.replyEmbeds(
                            getErrorEmbed("Invalid game mode specified.").build()
                    ).queue();
                    return;
                }


                bnChannel = e.getGuild().getTextChannelById(getRequestChannelIdByMode(mapRequest.mode));

                if (bnChannel == null) {
                    e.replyEmbeds(
                            getErrorEmbed("Failed to access the request channel. Please contact the staff.").build()
                    ).queue();
                    return;
                }


            }


        } catch (Exception ex) {
            AppLogger.log(ex.getLocalizedMessage(), LogLevel.ERROR);
        }

    }
}
